package com.supermall.controller.portal;

import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.User;
import com.supermall.service.IShoppingCartService;
import com.supermall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author
 */
@Controller
@RequestMapping("/shoppingcart/")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService iShoppingCartService;

    /**
     *  添加到购物车的功能
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer productId, Integer count){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.add(user.getId(),productId,count);
    }

    /**
     * 更新购物车
     * @param session
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer productId, Integer count){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.update(user.getId(),productId,count);
    }

    /**
     * 删除购物车中的商品
     * @param session
     * @param productIds
     * @return
     */
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.deleteProduct(user.getId(),productIds);
    }

    /**
     * 查询购物车
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.list(user.getId());
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping("selectAll.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.selectAndUnselect(user.getId(),null,Const.CartChecked.CHECKED);
    }

    /**
     * 全不选
     * @param session
     * @return
     */
    @RequestMapping("unSelectAll.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.selectAndUnselect(user.getId(),null,Const.CartChecked.UN_CHECKED);
    }

    /**
     * 单选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("selectSingle.do")
    @ResponseBody
    public ServerResponse<CartVo> selectSingle(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.selectAndUnselect(user.getId(),productId,Const.CartChecked.CHECKED);
    }

    /**
     * 反选
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("unSelectSingle.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectSingle(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShoppingCartService.selectAndUnselect(user.getId(),productId,Const.CartChecked.UN_CHECKED);
    }

    /**
     * 获取购物车中产品的总数
     * @param session
     * @return
     */
    @RequestMapping("getCartProductCount.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            //用户未登录则看到的数量是0
            return ServerResponse.createBySuccess(0);
        }
        return iShoppingCartService.getCartProductCount(user.getId());
    }





}
