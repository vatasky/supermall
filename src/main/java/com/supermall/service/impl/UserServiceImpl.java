package com.supermall.service.impl;

import com.supermall.common.Const;
import com.supermall.common.ServerResponse;
import com.supermall.common.TokenCache;
import com.supermall.dao.UserMapper;
import com.supermall.pojo.User;
import com.supermall.service.IUserService;
import com.supermall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * @author
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
        //首先要检验用户名是否存在
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            //返回值就是用户名已存在
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            //返回值就是邮箱已存在
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //对其密码进行MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username,String password){
        //登录时校验用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            //返回值就是用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //需要校验用户名和密码是否相对应
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //为什么要将密码置为空？
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功",user);
    }

    /**
     * 校验用户名和邮箱
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if (StringUtils.isNotBlank(type)){
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }

        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 忘记密码中的获取问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> forgetGetQuestion(String username){
        //判断用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            //返回值就是用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /**
     * 校对问题的答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount > 0){
            //说明问题的答案正确，生成一个token来缓存
            //生成uuid来进行缓存
            String forgerToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgerToken);
            return ServerResponse.createBySuccess(forgerToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案不正确");
    }

    /**
     * 忘记密码中的重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        //判断token是否为空
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        //判断用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            //返回值就是用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        //传递token值，检验是否一致
        if (StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updateByUsername(username,md5Password);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("密码修改成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取忘记密码的修改密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登录状态下的修改密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，需要对password进行校验，需要userI与password相对应
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("原密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码修改成功");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    /**
     * 根据用户id来查询信息
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<User> getUserInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateInformation(User user){
        //用户名不能修改
        //邮箱还需要重新校验，更新邮箱不能重复
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount > 0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0){
            return ServerResponse.createBySuccess("用户个人信息更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("用户个人信息更新失败");

    }

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkAdmin(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }





}