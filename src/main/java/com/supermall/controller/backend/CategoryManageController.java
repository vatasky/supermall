package com.supermall.controller.backend;

import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.User;
import com.supermall.service.ICategoryService;
import com.supermall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    /**
     * 添加物品种类及其分类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping(value = "addCategory.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //检验是否是管理员
        ServerResponse response = iUserService.checkAdmin(user);

        if (response.isSuccess()){
            //是管理员，处理增加分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    /**
     * 更新品类的名称
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "setCategoryName.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //检验是否是管理员
        ServerResponse response = iUserService.checkAdmin(user);

        if (response.isSuccess()){
            //是管理员，更新categoryName
            return iCategoryService.updateCategory(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取子类物品的详细信息,不递归
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "getChildrenParalleCategory.do")
    @ResponseBody
    public ServerResponse getChildrenParalleCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //检验是否是管理员
        ServerResponse response = iUserService.checkAdmin(user);

        if (response.isSuccess()){
            //是管理员，查询子类物品信息，不递归,保持平级
            return iCategoryService.getChildrenParalleCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 查询当前节点的id 并且递归子节点的id
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "getCategoryAndDeepChildrenCategory.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //检验是否是管理员
        ServerResponse response = iUserService.checkAdmin(user);

        if (response.isSuccess()){
            //是管理员，查询当前节点的id 并且递归子节点的id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }






}
