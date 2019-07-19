package com.supermall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.dao.CategoryMapper;
import com.supermall.dao.ProductMapper;
import com.supermall.pojo.Category;
import com.supermall.pojo.Product;
import com.supermall.service.ICategoryService;
import com.supermall.service.IProductService;
import com.supermall.util.DateTimeUtil;
import com.supermall.util.FastDFSUtil;
import com.supermall.util.PropertiesUtil;
import com.supermall.vo.ProductDetailVo;
import com.supermall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {


    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加或者更新产品
     * @param product
     * @return
     */
    @Override
    public ServerResponse addOrUpdateProduct(Product product){
        //判断产品是否存在
        if (product != null){
            //判断产品图片是否为空
            //根据判断情况，将图片的 第一张设置为主图
            if (StringUtils.isNotBlank(product.getSubImages())){
                //将所有图片放到一个字符串中，并用,号隔开
                String[] subImageArray = product.getSubImages().split(",");
                //判断字符串的长度，将第一张图设置为主图
                if (subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }

            //根据产品id是否存在 ，对产品进行添加或者更新
            if (product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccess("产品更新成功");
                }
                return ServerResponse.createByErrorMessage("产品更新失败");
            }else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccess("产品添加成功");
                }
                return ServerResponse.createByErrorMessage("产品添加失败");
            }

        }
        return ServerResponse.createByErrorMessage("添加或者更新的参数不正确");
    }

    /**
     * 设置销售状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse<String> setProductSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("产品销售状态修改成功");
        }
        return ServerResponse.createByErrorMessage("产品销售状态修改失败");
    }

    /**
     * 查看产品详情
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        //判断参数是否有异常
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //判断产品的状态
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("商品已经下架或者删除");
        }
        //需要返回一个Vo对象 value object 到前台展示信息
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    /**
     * value object 收集产品详情的Vo
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setPrice(product.getPrice());

        //imageHost 通过ftp服务器来获取图片
        productDetailVo.setImageHost(FastDFSUtil.IMG_URL);

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            //设置为默认值
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //createTime 在mybatis中拿
        // 来的是时候是个毫秒的数值，需要转变
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));

        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;

    }

    /**
     * 查询产品列表并进行动态分页  mybatis分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //查询并动态分页 有三个步骤 ： startPage  然后 进行sql查询的填充  最后进行 pageHelper的收尾
        //startPage--start
        PageHelper.startPage(pageNum,pageSize);
        //填充sql查询逻辑

        //创建vo拿到自己需要的产品信息反馈到前台
        List<ProductListVo> productListVoList = Lists.newArrayList();
        List<Product> productList = productMapper.selectProductList();
        for (Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //pageHelper---收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 收集产品列表Vo（value Object）
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }


    /**
     * 通过名称和id来搜索产品,并进行分页，mybatis分页
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> productSerch(String productName,Integer productId,int pageNum,int pageSize){
        //分页开始
        PageHelper.startPage(pageNum,pageSize);

        //字符串拼接,进行模糊查询
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        //创建vo拿到自己需要的产品信息反馈到前台
        List<ProductListVo> productListVoList = Lists.newArrayList();

        //sql逻辑 ，对产品进行搜索
        List<Product> productList = productMapper.selectProductByProductNameAndProductId(productName,productId);
        for (Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //pageHelper---收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }



   /**前台Service------------------------------------------------------------------------------------------------*/


    /**
     * 前台查看商品详情
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        //判断参数是否有异常
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //获取产品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("该商品已删除");
        }

        //前台在查看商品详情的时候需要确定此商品是否还处于在线状态
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品已经下架");
        }
        //需要返回一个Vo对象 value object 到前台展示信息
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 前台 通过名称 或者 分类id 来搜索产品
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductListByCategoryIdKyWord(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        //判断参数是否异常
        if (StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //创建一个catagoryId的数组，将分类的产品及其子产品的id都查出来--递归
        List<Integer> categoryIdList = new ArrayList<Integer>();

        //当categoryId存在时
        if (categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)){
                //没有此分类，并且没有关键字，返回空的结果集，不报错
                PageHelper.startPage(pageNum,pageSize);

                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //查到的 分类的id 结果集
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //排序处理,动态排序
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(orderBy)){
            //判断是升序还是降序
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                //分割成字符串数组
                String[] orderByArray = orderBy.split("_");
                System.out.print(orderByArray);
                //匹配 升序或者降序
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }

        //判断传的参数是否为空的，运用三元运算符来判断
        String newKyword = StringUtils.isBlank(keyword)?null:keyword;
        List<Integer> newCategoryIdList = categoryIdList.size()==0?null:categoryIdList;

        //获得查找之后的产品结果集
        List<Product> productList = productMapper.selectProductByNameAndCategoryIds(newKyword,newCategoryIdList);

        //通过Vo来获取前端想要的产品详情
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //分页结果处理
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }



















}
