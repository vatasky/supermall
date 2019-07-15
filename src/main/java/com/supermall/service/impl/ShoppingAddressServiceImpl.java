package com.supermall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.supermall.common.ServerResponse;
import com.supermall.dao.ShippingMapper;
import com.supermall.pojo.Shipping;
import com.supermall.service.IShoppingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author vatasky
 */
@Service("iShoppingAddressService")
public class ShoppingAddressServiceImpl implements IShoppingAddressService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 添加收货地址
     * @param userId
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping){
        //根据userId来添加地址
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0){

            //将添加地址成功的id返回到前端，用map存储方便获取
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createBySuccessMessage("新建地址失败");
    }
    @Override
    public ServerResponse<String> delete(Integer userId,Integer shippingId){
        /*要通过userId和shippingId一起来删除，防止横向越权的问题*/
        int resultCount = shippingMapper.deleteByUserIdAndShippingId(userId,shippingId);
        if (resultCount > 0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createBySuccessMessage("删除地址失败");

    }

    @Override
    public ServerResponse<String> update(Integer userId,Shipping shipping){
        /*要通过userId和shipping一起来更新，防止横向越权的问题*/

        /*设置userId为当前用户的id防止横向越权*/
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByShipping(shipping);
        if (resultCount > 0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createBySuccessMessage("更新地址失败");

    }
    @Override
    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        /*同样要做横向越权的处理*/
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        if (shipping == null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询地址成功",shipping);
    }
    @Override
    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        if (shippingList.size() == 0){
            return ServerResponse.createByErrorMessage("该用户没有添加收货地址");
        }
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
