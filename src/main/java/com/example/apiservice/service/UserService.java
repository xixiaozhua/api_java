package com.example.apiservice.service;

import com.example.apiservice.controller.UserController;
import com.example.apiservice.entity.Address;
import com.example.apiservice.entity.User;
import com.example.apiservice.repository.AddressRepository;
import com.example.apiservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            return null; // 用户不存在返回null
        }

        User user = userOpt.get();
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
        // 直接创建新用户，不再检查是否存在（调用方应该先检查）
        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(java.time.Instant.now());
        return userRepository.save(user);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
            .orElse(null);
    }

    public User authenticateUser(String phone, String password) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public List<Address> getUserAddresses(String phone) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            return List.of(); // 返回空列表而不是抛出异常
        }
        return addressRepository.findByPhone(userOpt.get().getPhone());
    }

    public void updateUserProfile(String phone, UserController.UpdateProfileRequest request) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            return; // 如果用户不存在，静默返回
        }
        
        User user = userOpt.get();
        user.setNickname(request.getNickname());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());
        
        userRepository.save(user);
    }

    public void updateAddress(String phone, Long addressId, UserController.AddressUpdateRequest request) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            return; // 如果用户不存在，静默返回
        }

        User user = userOpt.get();
        Optional<Address> addressOpt = addressRepository.findByIdAndUserId(addressId, user.getId());
        if (addressOpt.isEmpty()) {
            return; // 如果地址不存在，静默返回
        }

        Address address = addressOpt.get();
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
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            return; // 如果用户不存在，静默返回
        }
        
        User user = userOpt.get();
        Optional<Address> addressOpt = addressRepository.findByIdAndUserId(addressId, user.getId());
        if (addressOpt.isEmpty()) {
            return; // 如果地址不存在，静默返回
        }
        
        Address address = addressOpt.get();
        address.setIsDeleted(1);  // 软删除标记
        addressRepository.save(address);
    }

    public void deleteUser(String phone) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            return; // 如果用户不存在，静默返回
        }
        
        User user = userOpt.get();
        user.setIsDeleted(1);
        userRepository.save(user);
    }

    public boolean isUserExist(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    @Bean
    public ExecutorService addressThreadPool() {
        return Executors.newFixedThreadPool(10); // 建议添加队列容量和拒绝策略
    }
}