package com.example.apiservice.repository;

import com.example.apiservice.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByPhone(String phone);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void updateNonDefaultAddresses(@Param("userId") Long userId);

    @Query("SELECT a FROM Address a WHERE a.id = :id AND a.user.id = :userId AND a.isDeleted = 0")
    Optional<Address> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}