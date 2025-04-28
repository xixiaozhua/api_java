package com.example.apiservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.apiservice.entity.User;

@Mapper
public interface UserMapper {
    User selectById(Long id);
}