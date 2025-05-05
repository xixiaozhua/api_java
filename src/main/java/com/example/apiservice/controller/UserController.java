package com.example.apiservice.controller;

import com.example.apiservice.entity.User;
import com.example.apiservice.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.example.apiservice.common.enums.ResultCode;
import com.example.apiservice.entity.Address;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Data
    public static class ProfileRequest {
        private String nickname;
        private Integer gender;
        private String avatar;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        userService.updateUserProfile(phone, request);
        
        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", "更新成功");
                put("data", null);
            }}
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        User user = userService.getUserByPhone(phone);
        
        if (user == null) {
            return ResponseEntity.ok().body(
                new HashMap<String, Object>() {{
                    put("code", ResultCode.BAD_REQUEST.getCode());
                    put("message", "用户不存在");
                    put("data", null);
                }}
            );
        }

        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", ResultCode.SUCCESS.getMessage());
                put("data", new HashMap<String, Object>() {{
                    put("id", user.getId());
                    put("nickname", user.getNickname());
                    put("gender", user.getGender());
                    put("avatar_url", user.getAvatar());
                    put("email", user.getEmail());
                    put("phone", user.getPhone());
                    put("created_at", user.getCreatedAt());
                    put("updated_at", user.getUpdatedAt());
                }});
            }}
        );
    }


    @Data
    public static class AddressRequest {
        private String receiver;
        private String phone;
        private String province;
        private String city;
        private String district;
        private String address;
        private Boolean isDefault;
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getUserAddresses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();

        List<Address> addresses = userService.getUserAddresses(phone);

        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", ResultCode.SUCCESS.getMessage());
                put("data", addresses.stream()
                    .map(address -> new HashMap<String, Object>() {{
                        put("id", address.getId());
                        put("receiver", address.getReceiver());
                        put("phone", address.getPhone());
                        put("province", address.getProvince());
                        put("city", address.getCity());
                        put("district", address.getDistrict());
                        put("address", address.getAddress());
                        put("is_default", address.getIsDefault());
                    }})
                    .collect(Collectors.toList())
                );
            }}
        );
    }
    
    @PostMapping("/addresses")
    public ResponseEntity<?> createAddress(@RequestBody AddressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        Long addressId = userService.createAddress(phone, request);
        
        if (addressId == null) {
            return ResponseEntity.ok().body(
                new HashMap<String, Object>() {{
                    put("code", ResultCode.BAD_REQUEST.getCode());
                    put("message", "用户不存在");
                    put("data", null);
                }}
            );
        }
        
        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", "新增成功");
                put("data", new HashMap<String, Object>() {{
                    put("id", addressId);
                }});
            }}
        );
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        userService.updateAddress(phone, id, request);
        
        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", "更新成功");
                put("data", null);
            }}
        );
    }
    
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        userService.deleteAddress(phone, id);
        
        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", "删除成功");
                put("data", null);
            }}
        );
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        userService.deleteUser(phone);
        
        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", "删除成功");
                put("data", null);
            }}
        );
    }
    
    @Data
    public static class SetPasswordRequest {
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();

        userService.updateUserPassword(phone, request.password);

        return ResponseEntity.ok().body(
            new HashMap<String, Object>() {{
                put("code", ResultCode.SUCCESS.getCode());
                put("message", "密码设置成功");
                put("data", null);
            }}
        );
    }
}