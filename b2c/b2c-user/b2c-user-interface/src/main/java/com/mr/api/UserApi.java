package com.mr.api;

import com.mr.pojo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    /**
     * 校验用户名手机号是否可用
     * @param data
     * @param type
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public Boolean check(@PathVariable("data") String data,
                         @PathVariable("type") Integer type);
    /**
     * 注册用户
     * @param user
     * @param
     * @return
     */
    @PostMapping("register")
    public  ResponseEntity<Void> register(User user);
    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public User query(
            @RequestParam("username") String username,
            @RequestParam("password") String password);

}
