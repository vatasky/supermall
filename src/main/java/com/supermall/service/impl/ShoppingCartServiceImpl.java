package com.supermall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.dao.CartMapper;
import com.supermall.dao.ProductMapper;
import com.supermall.pojo.Cart;
import com.supermall.pojo.Product;
import com.supermall.service.IShoppingCartService;
import com.supermall.util.BigDecimalUtil;
import com.supermall.util.PropertiesUtil;
import com.supermall.vo.CartProductVo;
import com.supermall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 */
@Service("iShoppingCartService")
public class ShoppingCartServiceImpl implements IShoppingCartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加购物车
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查询购物车中的id为productId的商品
        Cart cart = cartMapper.selectByuserIdAndProductId(userId,productId);
        if (cart == null){
            //说明购物车是空的，需要添加产品
            Cart newCart = new Cart();
            newCart.setQuantity(count);
            newCart.setChecked(Const.CartChecked.CHECKED);
            newCart.setProductId(productId);
            newCart.setUserId(userId);
            cartMapper.insert(newCart);
        }else {
            //此时在购物车已经存在此产品
            //只需增加此商品的数量即可
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            //只需要更新购物车就可以了
            cartMapper.updateByPrimaryKeySelective(cart);
        }

        //添加购物车中的商品后，需要计算产品的库存，还有计算购物车中选中的产品的总价
        //在前端展示的购物车的详情，是需要user表的信息，product表的信息，还有cart表的信息的结合，
        // 所以需要创建一个Value Object来向前端展示
        //计算的详情通过getCartVoLimit方法来实现
        return this.list(userId);
    }

    /**
     * 更新购物车
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新库存，同时价格都要改变
        Cart cart = cartMapper.selectByuserIdAndProductId(userId,productId);
        if (cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }



    /**
     * 删除商品
     * @param userId
     * @param productIds
     * @return
     */
    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        //通过guava的Splitter的分割，然后通过splitToList转成集合
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteProductByUserIdAndProductIds(userId,productIdList);

        return this.list(userId);
    }

    /**
     * 查询购物车
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 全选，全不选，单选，反选
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectAndUnselect(Integer userId,Integer productId,Integer checked){
        cartMapper.selectAndUnselect(userId,productId,checked);
        return this.list(userId);
    }

    /**
     * 计算购物车中产品的总数
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }







    /**
     * 购物车中的产品库存限制和价格计算
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //通过userId来获取购物车里面的集合的信息
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //购物车总价初始化
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (!CollectionUtils.isEmpty(cartList)){
            for (Cart cartItem:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductPrice(product.getPrice());
                    //判断库存，用户需要购买的数量不能超过库存
                    //初始化购买的限制数量
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()){
                        //购买量没有超过库存,即库存充足
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.CartLimitCount.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.CartLimitCount.LIMIT_NUM_FAIL);

                        //购物车要更新有效库存
                        Cart cartQuantity = new Cart();
                        cartQuantity.setId(cartItem.getId());
                        cartQuantity.setQuantity(buyLimitCount);

                        cartMapper.updateByPrimaryKeySelective(cartQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);

                    //计算购物车中某一个产品的总价总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity().doubleValue()));
                    //将此产品进行勾选
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
            if (cartItem.getChecked() == Const.CartChecked.CHECKED){
                    //如果被勾选，增加到购物车的总价上
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
             //将重新赋值的对象放到数组中
            cartProductVoList.add(cartProductVo);
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;

    }

    /**
     * 是否全选
     * @param userId
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId){
        if (userId == null){
            return false;
        }
        int rowCount = cartMapper.selectCartProductCheckedStatusByUserId(userId);
        if (rowCount > 0){
            //说明购物车中的产品处于全选状态
            return false;
        }
        return  true;
    }




}
