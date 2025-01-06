package com.pdd.common.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;
import java.util.Date;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
public class JwtHelper {

    // token过期时间
    private static long tokenExpiration = 365*24*60*60*1000;
    // 加密密钥
    private static String tokenSignKey = "pdddd";

    // 根据 userId 和 userName 生成token字符串
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                // 设置分组
                .setSubject("pdddd-USER")
                // 设置token过期时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // jwt中的私有部分
                .claim("userId", userId)
                .claim("userName", userName)
                // 进行加密 HS512为编码加密方式 tokenSignKey是用于加密的密钥
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                // 对字符串进行压缩
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;
        // 根据密钥进行解码操作以获取userId
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
        // return 1L;
    }

    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }

    public static void removeToken(String token) {
        //jwttoken无需删除，客户端扔掉即可。
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(7L, "admin");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}
