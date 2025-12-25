package com.example.qtifood.services;

import com.example.qtifood.dtos.Shipping.ShippingFeeRequestDto;
import com.example.qtifood.dtos.Shipping.ShippingFeeResponseDto;

public interface ShippingService {
    /**
     * Tính phí ship dựa vào khoảng cách
     * @param request chứa tọa độ quán và địa chỉ nhận
     * @return phí ship chi tiết
     */
    ShippingFeeResponseDto calculateShippingFee(ShippingFeeRequestDto request);
    /**
     * Tính khoảng cách Haversine giữa 2 điểm (km)
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}
