package com.supermall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.supermall.common.ServerResponse;
import com.supermall.dao.CategoryMapper;
import com.supermall.pojo.Category;
import com.supermall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加物品的种类并分类
     * @param categoryName
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse addCategory(String categoryName,Integer parentId){

        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //表示这个分类是可用的
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0 ){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 更新品类的名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse updateCategory(Integer categoryId,String categoryName){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0 ){
            return ServerResponse.createBySuccess("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    /**
     * 获取子类物品的详细信息
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParalleCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return  ServerResponse.createBySuccess(categoryList);
    }


    /**
     * 递归查询本节点的id 及其 子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        //递归算法
        findChildrenCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null){
            for (Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，算出子节点
    //Set本身有排重
    private Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }

        //查找子节点 如果是空的就退出，递归算法一定要有一个退出条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem: categoryList){
            findChildrenCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }


}
