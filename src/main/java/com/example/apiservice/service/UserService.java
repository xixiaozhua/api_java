package com.example.apiservice.service;

import com.example.apiservice.controller.UserController;
import com.example.apiservice.entity.Address;
import com.example.apiservice.entity.User;
import com.example.apiservice.repository.AddressRepository;
import com.example.apiservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long createAddress(String phone, UserController.AddressCreateRequest request) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("User not found"));
    
        if (request.getIsDefault()) {
            addressRepository.updateNonDefaultAddresses(user.getId());
        }
    
        Address newAddress = new Address();
        newAddress.setUser(user);  // 使用JPA关系映射
        newAddress.setReceiver(request.getReceiver());
        newAddress.setPhone(request.getPhone());
        newAddress.setProvince(request.getProvince());
        newAddress.setCity(request.getCity());
        newAddress.setDistrict(request.getDistrict());
        newAddress.setAddress(request.getAddress());
        newAddress.setIsDefault(request.getIsDefault());
        newAddress.setCreatedAt(java.time.Instant.now());
    
        return addressRepository.save(newAddress).getId();
    }

    public User createUser(String phone, String password) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        // 添加创建时间赋值
        user.setCreatedAt(java.time.Instant.now());
        return userRepository.save(user);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public User authenticateUser(String phone, String password) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public List<Address> getUserAddresses(String phone) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        return addressRepository.findByPhone(user.getPhone());
    }

    public void updateUserProfile(String phone, UserController.UpdateProfileRequest request) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setNickname(request.getNickname());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());
        
        userRepository.save(user);
    }

    public void updateAddress(String phone, Long addressId, UserController.AddressUpdateRequest request) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new RuntimeException("地址不存在或不属于该用户"));

        if (request.getIsDefault()) {
            addressRepository.updateNonDefaultAddresses(user.getId());
        }

        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        address.setIsDefault(request.getIsDefault());
        address.setUpdatedAt(java.time.Instant.now());

        addressRepository.save(address);
    }

    public void deleteAddress(String phone, Long addressId) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new RuntimeException("地址不存在或无权操作"));
        
        address.setIsDeleted(1);  // 软删除标记
        addressRepository.save(address);
    }

    public void deleteUser(String phone) {
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setIsDeleted(1);
        userRepository.save(user);
    }
}