package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Shipping.ShippingFeeRequestDto;
import com.example.qtifood.dtos.Shipping.ShippingFeeResponseDto;
import com.example.qtifood.services.ShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class ShippingServiceImpl implements ShippingService {
    
    // Hằng số tính phí
    private static final BigDecimal BASE_FEE = BigDecimal.valueOf(15000);           // 15k cố định
    private static final BigDecimal FEE_3_5KM = BigDecimal.valueOf(4000);          // 4k/km
    private static final BigDecimal FEE_5_10KM = BigDecimal.valueOf(3000);         // 3k/km
    private static final BigDecimal FEE_ABOVE_10KM = BigDecimal.valueOf(2000);     // 2k/km

    @Override
    public ShippingFeeResponseDto calculateShippingFee(ShippingFeeRequestDto request) {
        // Tính khoảng cách Haversine
        double distance = calculateDistance(
                request.storeLatitude().doubleValue(),
                request.storeLongitude().doubleValue(),
                request.recipientLatitude().doubleValue(),
                request.recipientLongitude().doubleValue()
        );
        
        log.info("[ShippingService] Calculating fee for distance: {:.2f} km", distance);
        
        // Tính phí dựa vào khoảng cách
        BigDecimal baseFee = BASE_FEE;
        BigDecimal additionalFee = BigDecimal.ZERO;
        String description;
        
        if (distance <= 3.0) {
            // 0 - 3 km: 15k cố định
            additionalFee = BigDecimal.ZERO;
            description = String.format("Khoảng cách %.2f km (0-3km): Phí cố định 15.000đ", distance);
        } else if (distance <= 5.0) {
            // 3 - 5 km: + 4k/km cho khoảng cách vượt 3km
            double extraDistance = distance - 3.0;
            additionalFee = BigDecimal.valueOf(extraDistance).multiply(FEE_3_5KM)
                    .setScale(0, RoundingMode.HALF_UP);
            description = String.format("Khoảng cách %.2f km (3-5km): 15.000đ + %.2f km × 4.000đ = %s đ", 
                    distance, extraDistance, additionalFee.add(baseFee));
        } else if (distance <= 10.0) {
            // 5 - 10 km: 15k + 4k×2 (cho 3-5km) + 3k/km (cho phần vượt 5km)
            double distanceAfter5km = distance - 5.0;
            
            BigDecimal fee3to5 = BigDecimal.valueOf(2.0).multiply(FEE_3_5KM); // 2km × 4k
            BigDecimal feeAbove5 = BigDecimal.valueOf(distanceAfter5km).multiply(FEE_5_10KM)
                    .setScale(0, RoundingMode.HALF_UP);
            
            additionalFee = fee3to5.add(feeAbove5);
            description = String.format("Khoảng cách %.2f km (5-10km): 15.000đ + 8.000đ + %.2f km × 3.000đ = %s đ", 
                    distance, distanceAfter5km, additionalFee.add(baseFee));
        } else {
            // > 10 km: 15k + 8k (3-5km) + 15k (5-10km) + 2k/km (vượt 10km)
            double distanceAbove10 = distance - 10.0;
            
            BigDecimal fee3to5 = BigDecimal.valueOf(2.0).multiply(FEE_3_5KM);      // 2km × 4k = 8k
            BigDecimal fee5to10 = BigDecimal.valueOf(5.0).multiply(FEE_5_10KM);    // 5km × 3k = 15k
            BigDecimal feeAbove10 = BigDecimal.valueOf(distanceAbove10).multiply(FEE_ABOVE_10KM)
                    .setScale(0, RoundingMode.HALF_UP);
            
            additionalFee = fee3to5.add(fee5to10).add(feeAbove10);
            description = String.format("Khoảng cách %.2f km (>10km): 15.000đ + 8.000đ + 15.000đ + %.2f km × 2.000đ = %s đ", 
                    distance, distanceAbove10, additionalFee.add(baseFee));
        }
        
        BigDecimal totalFee = baseFee.add(additionalFee);
        
        log.info("[ShippingService] Distance: {:.2f}km, Base: {}đ, Additional: {}đ, Total: {}đ", 
                distance, baseFee, additionalFee, totalFee);
        
        return new ShippingFeeResponseDto(
                distance,
                baseFee,
                additionalFee,
                totalFee,
                description
        );
    }

    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính trái đất (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
}
