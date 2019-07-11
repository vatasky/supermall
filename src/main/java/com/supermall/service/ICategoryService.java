package com.supermall.service;

import com.supermall.common.ServerResponse;
import com.supermall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategory(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParalleCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
