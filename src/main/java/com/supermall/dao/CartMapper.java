package com.supermall.dao;

import com.supermall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByuserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteProductByUserIdAndProductIds(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    int selectAndUnselect(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    int getCartProductCount(Integer userId);
}