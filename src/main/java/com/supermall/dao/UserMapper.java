package com.supermall.dao;

import com.supermall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(@Param("username") String username);

    int checkEmail(@Param("email") String email);

    User selectLogin(@Param("username")String username,@Param("password")String password);

    String selectQuestionByUsername(@Param("username")String username);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    int updateByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    int checkPassword(@Param("userId") Integer userId,@Param("password") String password);

    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);

    //int checkUsername(String username);

    //int checkEmail(String email);

    //User selectLogin(@Param("username")String username,@Param("password") String password);

    //String selectQuestionByUsername(@Param("username")String username);

    //int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    //int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    //int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    //int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);*/
}