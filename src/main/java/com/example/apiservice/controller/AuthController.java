package com.example.apiservice.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;
import com.example.apiservice.common.enums.ResultCode;
import com.example.apiservice.entity.User;
import com.example.apiservice.service.SmsService;
import com.example.apiservice.service.UserService;
import com.example.apiservice.util.JwtUtil;
import com.example.apiservice.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final SmsService smsService;  // 修改为接口类型
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository, SmsService smsService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.smsService = smsService;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "手机号不能为空")
        private String phone;
        
        @NotBlank(message = "验证码不能为空")
        private String code;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 验证验证码
        if (!smsService.verifyCode(request.phone, request.code)) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", ResultCode.INVALID_CODE.getCode(),
                "message", "验证码错误或已过期"
            ));
        }
        
        // 优化后的存在性判断
        boolean userExists = userRepository.existsByPhone(request.phone);
        boolean isNewUser = !userExists;  // 新增判断逻辑
        
        User user = userExists ? 
            userRepository.findByPhone(request.phone).get() : 
            userService.createUser(request.phone);
        
        String token = jwtUtil.generateToken(user.getPhone());
        String message = isNewUser ? "注册成功" : "用户已存在，已成功登录";
        
        return ResponseEntity.ok(Map.of(
            "code", ResultCode.SUCCESS.getCode(),
            "message", message,
            "data", Map.of(
                "token", token,
                "user_id", user.getId(),
                "is_new_user", isNewUser
            )
        ));
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "手机号不能为空")
        private String phone;
        
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.authenticateUser(request.phone, request.password);
        String token = jwtUtil.generateToken(user.getPhone());
        
        return ResponseEntity.ok(Map.of(
            "code", ResultCode.SUCCESS.getCode(),
            "message", "登录成功",
            "data", Map.of(
                "token", token,
                "user_id", user.getId()
            )
        ));
    }

    @Data
    public static class SmsLoginRequest {
        @NotBlank(message = "手机号不能为空")
        private String phone;
        
        @NotBlank(message = "验证码不能为空")
        private String code;
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> request) {
        smsService.sendCode(request.get("phone"));
        return ResponseEntity.ok(Map.of(
            "code", ResultCode.SUCCESS.getCode(),
            "message", "验证码已发送"
        ));
    }

    @PostMapping("/sms-login")
    public ResponseEntity<?> smsLogin(@RequestBody SmsLoginRequest request) {
        if (!smsService.verifyCode(request.phone, request.code)) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", ResultCode.INVALID_CODE.getCode(),
                "message", "验证码错误或已过期"
            ));
        }

        User user = userService.getUserByPhone(request.phone);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", ResultCode.USER_NOT_FOUND.getCode(),
                "message", "用户不存在"
            ));
        }

        String token = jwtUtil.generateToken(user.getPhone());
        return ResponseEntity.ok(Map.of(
            "code", ResultCode.SUCCESS.getCode(),
            "message", "登录成功",
            "data", Map.of(
                "token", token,
                "user_id", user.getId()
            )
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of(
            "code", ResultCode.SUCCESS.getCode(),
            "message", "退出成功",
            "data", Map.of()
        ));
    }
}