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
    private Map<String, Claims> tokenCache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 1000; // 最大缓存数量
    
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.expiration}")
    private Long expiration;
    
    private Key signingKey;

    private Key getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
        return signingKey;
    }

    public String generateToken(String phone) {
        return Jwts.builder()
                .setSubject(phone)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        // 先尝试从缓存获取
        Claims cachedClaims = tokenCache.get(token);
        if (cachedClaims != null) {
            return cachedClaims;
        }
        
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 缓存解析结果，避免频繁解析相同的token
            if (tokenCache.size() < MAX_CACHE_SIZE) {
                tokenCache.put(token, claims);
            }
            
            return claims;
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
                Date expiration = entry.getValue().getExpiration();
                return expiration != null && expiration.getTime() < now;
            } catch (Exception e) {
                return true;
            }
        });
    }
}