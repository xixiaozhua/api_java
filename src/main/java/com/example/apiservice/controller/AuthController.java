package com.example.apiservice.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.example.apiservice.common.enums.ResultCode;
import com.example.apiservice.entity.User;
import com.example.apiservice.service.UserService;
import com.example.apiservice.util.JwtUtil;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "手机号不能为空")
        private String phone;
        
        @NotBlank(message = "验证码不能为空")
        private String code;
        
        @Size(min = 6, message = "密码至少6位")
        private String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.createUser(request.phone, request.password);
        String token = jwtUtil.generateToken(user.getPhone());
        
        return ResponseEntity.ok(Map.of(
            "code", ResultCode.SUCCESS.getCode(),
            "message", "注册成功",
            "data", Map.of(
                "token", token,
                "user_id", user.getId()
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
            "msg", "登录成功",
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
            "msg", "退出成功",
            "data", Map.of()
        ));
    }
}