package com.supermall.service;

import com.supermall.common.ServerResponse;
import com.supermall.pojo.User;


public interface IUserService {

    ServerResponse<String> register(User user);

    ServerResponse<User> login(String username,String password);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> getUserInformation(Integer userId);

    ServerResponse<User> updateInformation(User user);

    ServerResponse checkAdmin(User user);
}
