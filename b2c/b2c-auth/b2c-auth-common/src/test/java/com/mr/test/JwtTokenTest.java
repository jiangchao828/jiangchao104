package com.mr.test;

import com.mr.util.JwtUtils;
import com.mr.util.RsaUtils;
import com.mr.bo.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTokenTest {

    //公钥位置
    private static final String pubKeyPath = "D:\\b2c-jwt-key\\rea.pub";
    //私钥位置
    private static final String priKeyPath = "D:\\b2c-jwt-key\\rea.pri";
    //公钥对象
    private PublicKey publicKey;
    //私钥对象
    private PrivateKey privateKey;


    /**
     * 生成公钥私钥 根据密文
     * @throws Exception
     */
//    @Test
//    public void genRsaKey() throws Exception {
//        RsaUtils.generateKey(pubKeyPath, priKeyPath, "666666");
//    }


    /**
     * 从文件中读取公钥私钥
     * @throws Exception
     */
    @Before
    public void getKeyByRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 根据用户信息结合私钥生成token
     * @throws Exception
     */
    @Test
    public void genToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1l, "啦啦啦"), privateKey, 2);
        System.out.println("user-token = " + token);
    }


    /**
     * 结合公钥解析token
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiLllabllabllaYiLCJleHAiOjE2MDY0ODI1ODl9.I91FjgbqaKm3jCF-3b6Lnq7pZvj976vmtdMSj8nyDeoXu5YZFAoAx9XiYPxCxxAwVLTijzaSJ2OuSwlLCynt7p5lnqsUCTYYuBMQvL-mD6ASuhVRLd4aNnN50GmFbCbfLGXQ9t7Ln6KzNFh5yo7nVXHORwepqvpRekPTjoOHKrI";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}

