package com.example.apiservice.controller;

import com.example.apiservice.entity.User;
import com.example.apiservice.service.UserService;
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
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateProfileRequest request) {
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
    
    public static class UpdateProfileRequest {
        private String nickname;
        private Integer gender;
        private String avatar;
        
        // Getters and Setters
        public String getNickname() { return nickname; }
        public Integer getGender() { return gender; }
        public String getAvatar() { return avatar; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public void setGender(Integer gender) { this.gender = gender; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
    }
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        User user = userService.getUserByPhone(phone);
        
        // 修改响应体构建方式
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
                }});
            }}
        );
    }
    
    @PostMapping("/addresses")
    public ResponseEntity<?> createAddress(@RequestBody AddressCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        
        Long addressId = userService.createAddress(phone, request);
        
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
    
    public static class AddressCreateRequest {
        private String receiver;
        private String phone;
        private String province;
        private String city;
        private String district;
        private String address;
        private boolean isDefault;
        
        // Getters and Setters
        public String getReceiver() { return receiver; }
        public String getPhone() { return phone; }
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getAddress() { return address; }
        public boolean getIsDefault() { return isDefault; }
        public void setReceiver(String receiver) { this.receiver = receiver; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setProvince(String province) { this.province = province; }
        public void setCity(String city) { this.city = city; }
        public void setDistrict(String district) { this.district = district; }
        public void setAddress(String address) { this.address = address; }
        public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }
    }
    
    // 更新地址接口
    @PutMapping("/addresses/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressUpdateRequest request) {
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
    
    public static class AddressUpdateRequest {
        private String receiver;
        private String phone;
        private String province;
        private String city;
        private String district;
        private String address;
        private boolean isDefault;
        
        public String getReceiver() { return receiver; }
        public String getPhone() { return phone; }
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getAddress() { return address; }
        public boolean getIsDefault() { return isDefault; }
        public void setReceiver(String receiver) { this.receiver = receiver; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setProvince(String province) { this.province = province; }
        public void setCity(String city) { this.city = city; }
        public void setDistrict(String district) { this.district = district; }
        public void setAddress(String address) { this.address = address; }
        public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }
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
}