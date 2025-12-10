package com.example.qtifood.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.qtifood.entities.Driver;
import com.example.qtifood.enums.VerificationStatus;

public interface DriverRepository extends JpaRepository<Driver, String> {
    
    Optional<Driver> findByPhone(String phone);
    
    boolean existsByPhone(String phone);
    
        boolean existsByVehiclePlate(String vehiclePlate);
        boolean existsByVehiclePlateAndIdNot(String vehiclePlate, String id);
    boolean existsByPhoneAndIdNot(String phone, String id);
    
    boolean existsByCccdNumber(String cccdNumber);
    
    boolean existsByCccdNumberAndIdNot(String cccdNumber, String id);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    boolean existsByLicenseNumberAndIdNot(String licenseNumber, String id);
    
    List<Driver> findByVerificationStatus(VerificationStatus verificationStatus);
    
    List<Driver> findByVerified(Boolean verified);
    
    List<Driver> findByVerificationStatusAndVerified(VerificationStatus verificationStatus, Boolean verified);
    
    @Query("SELECT d FROM Driver d WHERE LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Driver> searchByName(@Param("name") String name);
    
    @Query("SELECT d FROM Driver d WHERE d.phone LIKE %:phone%")
    List<Driver> searchByPhone(@Param("phone") String phone);
    
    @Query("SELECT d FROM Driver d WHERE LOWER(d.vehicleType) LIKE LOWER(CONCAT('%', :vehicleType, '%'))")
    List<Driver> findByVehicleType(@Param("vehicleType") String vehicleType);
}