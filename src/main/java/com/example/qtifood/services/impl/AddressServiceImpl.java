package com.example.qtifood.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.Addresses.AddressResponseDto;
import com.example.qtifood.dtos.Addresses.CreateAddressDto;
import com.example.qtifood.dtos.Addresses.UpdateAddressDto;
import com.example.qtifood.entities.Address;
import com.example.qtifood.entities.User;
import com.example.qtifood.mappers.AddressMapper;
import com.example.qtifood.repositories.AddressRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponseDto createAddress(CreateAddressDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));

        Address address = Address.builder()
                .receiver(dto.getReceiver())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                // Nếu entity dùng BigDecimal cho tọa độ:
                .latitude(dto.getLatitude() == null ? null : BigDecimal.valueOf(dto.getLatitude()))
                .longitude(dto.getLongitude() == null ? null : BigDecimal.valueOf(dto.getLongitude()))
                .isDefault(false)
                .user(user)
                .build();

        Address saved = addressRepository.save(address);
        return AddressMapper.toDto(saved);
    }

    @Override
    public AddressResponseDto updateAddress(Long id, UpdateAddressDto dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found: " + id));

        if (dto.getReceiver() != null)   address.setReceiver(dto.getReceiver());
        if (dto.getPhone() != null)      address.setPhone(dto.getPhone());
        if (dto.getAddress() != null)    address.setAddress(dto.getAddress());
        if (dto.getLatitude() != null)   address.setLatitude(BigDecimal.valueOf(dto.getLatitude()));
        if (dto.getLongitude() != null)  address.setLongitude(BigDecimal.valueOf(dto.getLongitude()));

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            applyDefaultForUser(address);
        }

        Address updated = addressRepository.save(address);
        return AddressMapper.toDto(updated);
    }

    @Override
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found: " + id);
        }
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll()
                .stream().map(AddressMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId)
                .stream().map(AddressMapper::toDto).toList();
    }

    @Override
    public AddressResponseDto setDefaultAddress(Long id) {
        Address a = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found: " + id));

    addressRepository.unsetOthersDefault(a.getUser().getId(), a.getId());
        a.setIsDefault(true);

        return AddressMapper.toDto(addressRepository.save(a));
    }

    @Override
    public AddressResponseDto setUnDefaultAddress(Long id) {
        Address a = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found: " + id));

        a.setIsDefault(false);
        return AddressMapper.toDto(addressRepository.save(a));
    }

    private void applyDefaultForUser(Address target) {
    String userId = target.getUser().getId();
    List<Address> all = addressRepository.findByUserId(userId);
        for (Address a : all) {
            a.setIsDefault(a.getId().equals(target.getId()));
        }
        addressRepository.saveAll(all);
    }
}
