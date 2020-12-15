package com.mr.service;

import com.mr.bo.UserInfo;
import com.mr.client.UserClient;
import com.mr.config.JwtConfig;
import com.mr.pojo.User;
import com.mr.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 校验账号密码，如果正确，组装token返回
     * @param username
     * @param password
     * @return
     */
     public String auth(String username,String password){
         try {
                 //调用用户中心服务，校验账号密码
                User user=userClient.query(username,password);
                //查询失败，账号密码错误，返回null
                if(user ==null ){ 
                    return null;
                 }
                 //不为null就组装用户载荷对象 生成token
                 UserInfo userInfo=new UserInfo(user.getId(), user.getUsername());
             
                 String token = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());
                 return token;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }

     }
}