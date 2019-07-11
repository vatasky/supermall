package com.supermall.service;

import com.github.pagehelper.PageInfo;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.Product;
import com.supermall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse addOrUpdateProduct(Product product);

    ServerResponse<String> setProductSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> productSerch(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductListByCategoryIdKyWord(String keyWord,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
