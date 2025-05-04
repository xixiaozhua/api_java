package com.example.apiservice.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public void sendCode(String phone) {
        String code = String.format("%04d", new Random().nextInt(9000) + 1000);
        String cacheKey = "sms_code:" + phone;
        redisTemplate.opsForValue().set(cacheKey, code, 5, TimeUnit.MINUTES);
    }
    
    @Override
    public boolean verifyCode(String phone, String code) {
        // 保留万能验证码逻辑
        if ("9999".equals(code)) return true;
        
        // 添加空指针保护
        if (code == null) return false;
        
        String cacheKey = "sms_code:" + phone;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        return code.equals(cachedCode);
    }
}