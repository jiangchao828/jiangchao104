package com.mr.service;

import com.mr.mapper.UserMapper;
import com.mr.pojo.User;
import com.mr.util.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public Boolean validNameAndPhone(String data,Integer type){
        User user=new User();
        if(type==1){
            user.setUsername(data);
        }else  if(type==2){
            user.setPhone(data);
        }
        return  userMapper.selectCount(user)==0;
    }
    public  Boolean register(User user){
        //设置注册时间
        user.setCreated(new Date());
        //设置uuid为盐
        user.setSalt(Md5Utils.generateSalt());
        //将原始密码进行加密，配合盐再次加密，防止破解
        user.setPassword(Md5Utils.md5Hex(user.getPassword(),user.getSalt()));
        //如果返回结果1 为成功，0为失败
        return   userMapper.insert(user)==1;
    }
    public  User query(String username,String password){
        User ex=new User();
        ex.setUsername(username);
        User user=userMapper.selectOne(ex);
        //根据用户名查询用户，校验用户名，密码是否正确
        if(user==null){
            return null;
            //原始密码加盐加密后，在与数据库密码进行对比
        } else if(!user.getPassword().equals(Md5Utils.md5Hex(password,user.getSalt()))){
            return null;
        }
        return user;
    }
}
