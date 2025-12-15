package com.example.qtifood.services.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.qtifood.services.BannerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled task để tự động cập nhật status của banners đã hết hạn
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BannerScheduler {

    private final BannerService bannerService;

    /**
     * Chạy mỗi giờ để kiểm tra và cập nhật banners hết hạn
     * Cron: 0 0 * * * * = Chạy vào đầu mỗi giờ
     */
    @Scheduled(cron = "0 0 * * * *")
    public void updateExpiredBanners() {
        log.info("Running scheduled task to update expired banners");
        try {
            bannerService.updateExpiredBanners();
            log.info("Successfully updated expired banners");
        } catch (Exception e) {
            log.error("Error updating expired banners: {}", e.getMessage(), e);
        }
    }
}
