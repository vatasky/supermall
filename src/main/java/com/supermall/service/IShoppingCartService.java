package com.supermall.service;

import com.supermall.common.ServerResponse;
import com.supermall.vo.CartVo;

public interface IShoppingCartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectAndUnselect(Integer userId,Integer productId,Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
