package com.mr.controller;


import com.item.common.utils.CookieUtils;
import com.mr.bo.UserInfo;
import com.mr.config.JwtConfig;
import com.mr.service.AuthService;
import com.mr.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            HttpServletRequest request,
            HttpServletResponse response){
        //校验账号密码是否正确
            String token=authService.auth(username,password);
        //如果token证明账号密码错误，返回401无权限
        if (StringUtils.isEmpty(token) ){
          return   new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //如果token有值 就放入cookie中
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);
        return ResponseEntity.ok(null);
    }
    /**
     * 解析token数据
     *
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("B2C_TOKEN") String token,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        try {
            // 获取token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());


            // 可以解析token的证明用户是正确登陆状态，重新生成token，这样登陆状态就又刷新30分钟过期了
            String newToken = JwtUtils.generateToken(userInfo,
                    jwtConfig.getPrivateKey(), jwtConfig.getExpire());


            // 将新token写入cookie 过期时间延长了
            CookieUtils.setCookie(request, response, jwtConfig.getCookieName(),
                    newToken, jwtConfig.getCookieMaxAge(), true);
            // 成功后直接返回
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // 抛出异常，证明token无效，直接返回403
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}