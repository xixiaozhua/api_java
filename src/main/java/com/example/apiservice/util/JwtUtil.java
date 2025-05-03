package com.example.apiservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // 添加缓存，提高性能
    private Map<String, String> tokenCache = new ConcurrentHashMap<>();  // 修改为String缓存
    
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.expiration}")
    private Long expiration;
    
    private final Key signingKey;  // 改为final字段

    public JwtUtil(
        @Value("${spring.jwt.secret}") String secret,
        @Value("${spring.jwt.expiration}") Long expiration) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());  // 需要先初始化secret字段
        this.secret = secret;  // 调整顺序
        this.expiration = expiration;
    }

    public String generateToken(String phone) {
        String cacheKey = phone + "_" + (System.currentTimeMillis() / expiration);
        return tokenCache.computeIfAbsent(cacheKey, k -> 
            Jwts.builder()
                .setSubject(phone)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey)
                .compact()
        );
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)  // 直接使用初始化好的密钥
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("JWT解析错误", e);
            return null;
        }
    }

    // 清理过期的缓存条目
    public void cleanCache() {
        long now = System.currentTimeMillis();
        tokenCache.entrySet().removeIf(entry -> {
            try {
                // 解析token获取过期时间
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(entry.getValue())
                    .getBody();
                return claims.getExpiration().getTime() < now;
            } catch (Exception e) {
                return true;
            }
        });
    }
}