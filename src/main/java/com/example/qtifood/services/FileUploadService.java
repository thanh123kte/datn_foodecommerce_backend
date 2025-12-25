package com.example.qtifood.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file, String entityType, String entityId);
    void deleteFile(String filePath);
    boolean isImageFile(MultipartFile file);
}