package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Banners.BannerResponseDto;
import com.example.qtifood.dtos.Banners.CreateBannerDto;
import com.example.qtifood.dtos.Banners.UpdateBannerDto;
import com.example.qtifood.entities.Banner;
import com.example.qtifood.enums.BannerStatus;
import com.example.qtifood.repositories.BannerRepository;
import com.example.qtifood.services.BannerService;
import com.example.qtifood.services.FileUploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final FileUploadService fileUploadService;

    @Override
    public BannerResponseDto createBanner(CreateBannerDto dto) {
        // Validate date range
        validateDateRange(dto.startDate(), dto.endDate());
        
        Banner banner = Banner.builder()
                .title(dto.title())
                .imageUrl(dto.imageUrl())
                .description(dto.description())
                .status(dto.status() != null ? dto.status() : BannerStatus.ACTIVE)
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toDto(bannerRepository.save(banner));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDto> getAllBanners() {
        return bannerRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponseDto getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found: " + id));
        return toDto(banner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDto> getBannersByStatus(BannerStatus status) {
        return bannerRepository.findByStatus(status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BannerResponseDto updateBanner(Long id, UpdateBannerDto dto) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found: " + id));

        // Validate new date range if provided
        LocalDateTime newStartDate = dto.startDate() != null ? dto.startDate() : banner.getStartDate();
        LocalDateTime newEndDate = dto.endDate() != null ? dto.endDate() : banner.getEndDate();
        validateDateRange(newStartDate, newEndDate);

        if (dto.title() != null)       banner.setTitle(dto.title());
        if (dto.imageUrl() != null)    banner.setImageUrl(dto.imageUrl());
        if (dto.description() != null) banner.setDescription(dto.description());
        if (dto.status() != null)      banner.setStatus(dto.status());
        if (dto.startDate() != null)   banner.setStartDate(dto.startDate());
        if (dto.endDate() != null)     banner.setEndDate(dto.endDate());
        banner.setUpdatedAt(LocalDateTime.now());

        return toDto(bannerRepository.save(banner));
    }

    @Override
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found: " + id));
        
        // Delete image file if exists - MUST delete image before deleting banner
        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            try {
                fileUploadService.deleteFile(banner.getImageUrl());
                System.out.println("Successfully deleted banner image: " + banner.getImageUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete banner image: " + e.getMessage());
                // Continue to delete banner even if image deletion fails
                // to avoid orphaned database records
            }
        }
        
        // Delete banner from database
        bannerRepository.deleteById(id);
        System.out.println("Successfully deleted banner with id: " + id);
    }

    @Override
    public BannerResponseDto uploadImage(Long id, MultipartFile imageFile) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found: " + id));

        // Validate file type
        if (!fileUploadService.isImageFile(imageFile)) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Delete old image if exists
        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            try {
                fileUploadService.deleteFile(banner.getImageUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete old banner image: " + e.getMessage());
            }
        }

        // Upload new image
        String imageUrl = fileUploadService.uploadFile(imageFile, "banners", id.toString());
        banner.setImageUrl(imageUrl);
        banner.setUpdatedAt(LocalDateTime.now());

        return toDto(bannerRepository.save(banner));
    }

    @Override
    public BannerResponseDto deleteImage(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found: " + id));

        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            try {
                fileUploadService.deleteFile(banner.getImageUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete banner image: " + e.getMessage());
            }
        }

        banner.setImageUrl(null);
        banner.setUpdatedAt(LocalDateTime.now());

        return toDto(bannerRepository.save(banner));
    }

    @Override
    public void updateExpiredBanners() {
        List<Banner> expiredBanners = bannerRepository.findExpiredBanners(LocalDateTime.now());
        for (Banner banner : expiredBanners) {
            banner.setStatus(BannerStatus.EXPIRED);
            banner.setUpdatedAt(LocalDateTime.now());
        }
        if (!expiredBanners.isEmpty()) {
            bannerRepository.saveAll(expiredBanners);
        }
    }

    /**
     * Validate date range: startDate must be before endDate
     */
    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
        }
    }

    private BannerResponseDto toDto(Banner banner) {
        return new BannerResponseDto(
                banner.getId(),
                banner.getTitle(),
                banner.getImageUrl(),
                banner.getDescription(),
                banner.getStatus(),
                banner.getStartDate(),
                banner.getEndDate(),
                banner.getCreatedAt(),
                banner.getUpdatedAt()
        );
    }
}
