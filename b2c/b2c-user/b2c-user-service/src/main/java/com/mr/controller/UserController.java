package com.mr.controller;

import com.mr.pojo.User;
import com.mr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 校验用户名手机号是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data,
                                         @PathVariable("type") Integer type){
        //  参数不全，返回失败的请求
        if(data ==null || type ==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Boolean resu=  userService.validNameAndPhone(data,type);

        return ResponseEntity.ok(resu);
    }
    /**
     * 注册用户
     * @param user
     * @param
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(User user){
        boolean resu=  userService.register(user);
        //注册失败，返回400请求
        if(!resu){
            return  new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> query(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        User user=userService.query(username,password);
        if(user==null){
//           "查询结果为null" 返回400请求
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok(user);
    }
}
