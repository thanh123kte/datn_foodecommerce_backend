package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Shipping.ShippingFeeRequestDto;
import com.example.qtifood.dtos.Shipping.ShippingFeeResponseDto;
import com.example.qtifood.services.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {
    
    
    private final ShippingService shippingService;
    
    /**
     * Tính phí ship dựa vào tọa độ quán và địa chỉ nhận
     * 
     * @param request chứa:
     *   - storeLatitude: vĩ độ quán
     *   - storeLongitude: kinh độ quán
     *   - recipientLatitude: vĩ độ địa chỉ nhận
     *   - recipientLongitude: kinh độ địa chỉ nhận
     * @return phí ship chi tiết gồm:
     *   - distanceKm: khoảng cách
     *   - baseFee: phí cơ bản (15k)
     *   - additionalFee: phí bổ sung dựa vào khoảng cách
     *   - totalFee: tổng phí
     *   - description: mô tả chi tiết cách tính
     */
    @PostMapping("/calculate-fee")
    public ResponseEntity<ShippingFeeResponseDto> calculateShippingFee(
            @RequestBody ShippingFeeRequestDto request) {
        
        ShippingFeeResponseDto response = shippingService.calculateShippingFee(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Tính khoảng cách giữa 2 điểm (cho debug)
     */
    @GetMapping("/distance")
    public ResponseEntity<Double> getDistance(
            @RequestParam Double lat1,
            @RequestParam Double lon1,
            @RequestParam Double lat2,
            @RequestParam Double lon2) {
        
        double distance = shippingService.calculateDistance(lat1, lon1, lat2, lon2);
        return ResponseEntity.ok(distance);
    }
}
