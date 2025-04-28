package com.example.apiservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.apiservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
}