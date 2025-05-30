package com.example.apiservice.auth;

import com.example.apiservice.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.jsonwebtoken.Claims;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    
    // 使用线程局部变量缓存正在处理的token信息，避免同一请求中多次解析
    private final ThreadLocal<String> currentToken = new ThreadLocal<>();
    
    // 使用内存缓存存储已验证过的token
    private final Map<String, String> validTokens = new ConcurrentHashMap<>(1000);

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    // 放行验证码登录和注册接口
    if (request.getServletPath().startsWith("/api/v1/auth/sms-login") 
    || request.getServletPath().startsWith("/api/v1/auth/register")) {
    filterChain.doFilter(request, response);
    return;
    }

    try {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
                
                // 检查是否已缓存该token
                String cachedPhone = validTokens.get(token);
                if (cachedPhone != null) {
                    setAuthentication(cachedPhone, request);
                } else {
                    // 设置当前线程处理的token
                    currentToken.set(token);
                    
                    Claims claims = jwtUtil.parseToken(token);
                    if (claims != null) {
                        String phone = claims.getSubject();
                        setAuthentication(phone, request);
                        
                        // 缓存有效token
                        if (validTokens.size() < 1000) {
                            validTokens.put(token, phone);
                        }
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            // 清理线程局部变量
            currentToken.remove();
        }

        
    }
    
    private void setAuthentication(String phone, HttpServletRequest request) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        phone, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}