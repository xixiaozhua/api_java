package com.example.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
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
    @Column(name = "is_default", columnDefinition = "TINYINT(1) default 0")
    private Boolean isDefault;
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) default 0")
    private Boolean isDeleted = false;
    private Instant createdAt;
    private Instant updatedAt;
}