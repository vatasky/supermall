package com.supermall.service;

import com.github.pagehelper.PageInfo;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.Shipping;

/**
 * @author vatasky
 */
public interface IShoppingAddressService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String> delete(Integer userId,Integer shippingId);

    ServerResponse<String> update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
