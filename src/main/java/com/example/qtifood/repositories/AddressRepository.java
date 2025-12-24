package com.example.qtifood.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.qtifood.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(String userId);
    List<Address> findByIsDeletedFalse();
    List<Address> findByUserIdAndIsDeletedFalse(String userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Address a set a.isDefault = false where a.user.id = :userId and a.id <> :keepId")
    void unsetOthersDefault(@Param("userId") String userId, @Param("keepId") Long keepId);
}
