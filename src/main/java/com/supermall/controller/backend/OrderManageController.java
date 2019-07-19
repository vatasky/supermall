package com.supermall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.User;
import com.supermall.service.IOrderService;
import com.supermall.service.IUserService;
import com.supermall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author vatasky
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /**
     * 后台订单lieb
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("orderList.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //填充业务逻辑
            return iOrderService.manageList(pageNum,pageSize);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * 后台订单详情
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //填充业务逻辑
            return iOrderService.manageDetail(orderNo);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * 后台搜索，分页，模糊查询
     * @param session
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session, Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //填充业务逻辑
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    /**
     * 后天发货功能
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("sendGoods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要管理员登录");
        }
        //判断是否是管理员登录
        ServerResponse response = iUserService.checkAdmin(user);
        if (response.isSuccess()){
            //填充业务逻辑
            return iOrderService.manageSendGoods(orderNo);

        }else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }














}
