package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.qtifood.dtos.Banners.BannerResponseDto;
import com.example.qtifood.dtos.Banners.CreateBannerDto;
import com.example.qtifood.dtos.Banners.UpdateBannerDto;
import com.example.qtifood.enums.BannerStatus;
import com.example.qtifood.services.BannerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * Tạo banner mới
     * POST /api/banners
     */
    @PostMapping
    public ResponseEntity<BannerResponseDto> createBanner(@Valid @RequestBody CreateBannerDto dto) {
        BannerResponseDto banner = bannerService.createBanner(dto);
        return ResponseEntity.ok(banner);
    }

    /**
     * Lấy tất cả banners
     * GET /api/banners
     */
    @GetMapping
    public ResponseEntity<List<BannerResponseDto>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBanners());
    }

    /**
     * Lấy banner theo id
     * GET /api/banners/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BannerResponseDto> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.getBannerById(id));
    }

    /**
     * Lấy banners theo status
     * GET /api/banners/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BannerResponseDto>> getBannersByStatus(@PathVariable BannerStatus status) {
        return ResponseEntity.ok(bannerService.getBannersByStatus(status));
    }

    /**
     * Cập nhật banner
     * PUT /api/banners/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<BannerResponseDto> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBannerDto dto) {
        BannerResponseDto updated = bannerService.updateBanner(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa banner
     * DELETE /api/banners/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok("Banner " + id + " deleted successfully.");
    }

    /**
     * Upload ảnh cho banner
     * POST /api/banners/{id}/image
     */
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<BannerResponseDto> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {
        
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        BannerResponseDto updatedBanner = bannerService.uploadImage(id, imageFile);
        return ResponseEntity.ok(updatedBanner);
    }

    /**
     * Xóa ảnh của banner
     * DELETE /api/banners/{id}/image
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<BannerResponseDto> deleteImage(@PathVariable Long id) {
        BannerResponseDto updatedBanner = bannerService.deleteImage(id);
        return ResponseEntity.ok(updatedBanner);
    }
}
