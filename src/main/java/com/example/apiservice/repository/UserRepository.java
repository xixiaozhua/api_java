package com.example.apiservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.apiservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 已存在的查询方法
    Optional<User> findByPhone(String phone);
    
    // 新增存在性判断方法
    boolean existsByPhone(String phone);
}