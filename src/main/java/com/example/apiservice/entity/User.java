package com.example.apiservice.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String phone;
    private String password;
    private String nickname;
    private Integer gender;
    private String avatar;
    private String email;
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;
    private Integer isDeleted = 0;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public Integer getGender() { return gender; }
    public String getAvatar() { return avatar; }
    public String getEmail() { return email; }
    public List<Address> getAddresses() { return addresses; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public java.time.Instant getUpdatedAt() { return updatedAt; }
    public Integer getIsDeleted() { return isDeleted; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setGender(Integer gender) { this.gender = gender; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setEmail(String email) { this.email = email; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdateAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}