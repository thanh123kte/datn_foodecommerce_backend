package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.Addresses.AddressResponseDto;
import com.example.qtifood.dtos.Addresses.CreateAddressDto;
import com.example.qtifood.dtos.Addresses.UpdateAddressDto;

public interface AddressService {
    AddressResponseDto createAddress(CreateAddressDto dto);
    AddressResponseDto updateAddress(Long id, UpdateAddressDto dto);
    void deleteAddress(Long id);
    List<AddressResponseDto> getAllAddresses();
    AddressResponseDto getAddressById(Long id);
    List<AddressResponseDto> getAddressesByUserId(String userId);
    AddressResponseDto setDefaultAddress(Long id);
    AddressResponseDto setUnDefaultAddress(Long id);
}
