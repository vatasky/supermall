package com.supermall.controller.portal;

import com.supermall.common.Const;
import com.supermall.common.ResponseCode;
import com.supermall.common.ServerResponse;
import com.supermall.pojo.User;
import com.supermall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**
 * @author
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);

    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 校验用户名和邮箱
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 通过用户名来查找设置的问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forgetGetQuestion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetQuestion(username);
    }

    /**
     * 通过用户名和问题来校验问题的答案是否正确，如果正确则并返回一段token字符用来重置密码
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forgetCheckAnswer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 通过用户名和token来重新设置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forgetResetPassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态下的修改密码
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "resetPassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * 登录状态下的用户查询信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getUserInformation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInformation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录status=10");
        }
        return iUserService.getUserInformation(currentUser.getId());
    }


    /**
     * 用户个人信息更新
0     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "updateUserInformation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInformation(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;

    }


}
