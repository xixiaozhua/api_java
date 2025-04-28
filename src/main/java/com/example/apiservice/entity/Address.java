package com.example.apiservice.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String address;
    private Boolean isDefault;
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;
    private Integer isDeleted = 0;
    // Getters and Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getReceiver() { return receiver; }
    public String getPhone() { return phone; }
    public String getProvince() { return province; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getAddress() { return address; }
    public Boolean getIsDefault() { return isDefault; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public java.time.Instant getUpdatedAt() { return updatedAt; }
    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setUser(User user) { this.user = user; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProvince(String province) { this.province = province; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setAddress(String address) { this.address = address; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}