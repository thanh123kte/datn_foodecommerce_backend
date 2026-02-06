package com.example.qtifood.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.qtifood.dtos.Banners.BannerResponseDto;
import com.example.qtifood.dtos.Banners.CreateBannerDto;
import com.example.qtifood.dtos.Banners.UpdateBannerDto;
import com.example.qtifood.enums.BannerStatus;

public interface BannerService {

    // Tạo mới banner
    BannerResponseDto createBanner(CreateBannerDto dto);

    // Lấy tất cả banners
    List<BannerResponseDto> getAllBanners();

    // Lấy banner theo id
    BannerResponseDto getBannerById(Long id);

    // Lấy banners theo status
    List<BannerResponseDto> getBannersByStatus(BannerStatus status);

    // Cập nhật banner
    BannerResponseDto updateBanner(Long id, UpdateBannerDto dto);

    // Xóa banner
    void deleteBanner(Long id);

    // Upload image
    BannerResponseDto uploadImage(Long id, MultipartFile imageFile);

    // Delete image
    BannerResponseDto deleteImage(Long id);

    // Tự động cập nhật status của các banner đã hết hạn
    void updateExpiredBanners();
}
