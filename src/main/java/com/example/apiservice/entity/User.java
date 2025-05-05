package com.example.apiservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
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
    private Instant createdAt;
    private Instant updatedAt;
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) default 0")
    private Boolean isDeleted = false;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
}