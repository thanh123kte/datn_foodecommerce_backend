    
package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.Stores.*;
import com.example.qtifood.enums.StoreStatus;
import com.example.qtifood.services.StoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Validated
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponseDto> create(@Valid @RequestBody CreateStoreDto dto) {
        return ResponseEntity.ok(storeService.createStore(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateStoreDto dto) {
        return ResponseEntity.ok(storeService.updateStore(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StoreResponseDto>> getAll() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<StoreResponseDto> byOwner(@PathVariable String ownerId) {
        List<StoreResponseDto> stores = storeService.getStoresByOwner(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Trả về store đầu tiên vì một owner chỉ có một store
        return ResponseEntity.ok(stores.get(0));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StoreResponseDto>> search(@RequestParam("q") String q) {
        return ResponseEntity.ok(storeService.searchByName(q));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<StoreResponseDto>> byStatus(@PathVariable StoreStatus status) {
        return ResponseEntity.ok(storeService.getStoresByStatus(status));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<StoreResponseDto> setStatus(@PathVariable Long id,
                                                      @PathVariable StoreStatus status) {
        return ResponseEntity.ok(storeService.setStatus(id, status));
    }

    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<StoreResponseDto> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") org.springframework.web.multipart.MultipartFile imageFile) {
        
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        StoreResponseDto updatedStore = storeService.uploadImage(id, imageFile);
        return ResponseEntity.ok(updatedStore);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<StoreResponseDto> deleteImage(@PathVariable Long id) {
        StoreResponseDto updatedStore = storeService.deleteImage(id);
        return ResponseEntity.ok(updatedStore);
    }

    /**
     * Tăng lượt xem store khi user mở chi tiết
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<StoreResponseDto> incrementView(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.incrementView(id));
    }

    // Lấy danh sách cửa hàng gần user, sắp xếp tăng dần theo km
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyStoreDto>> getNearbyStores(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(storeService.getNearbyStores(lat, lng));
    }
}
