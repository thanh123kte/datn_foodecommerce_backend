package com.example.qtifood.services.impl;

import com.example.qtifood.config.UploadConfig;
import com.example.qtifood.exceptions.FileUploadException;
import com.example.qtifood.services.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final UploadConfig uploadConfig;

    @Override
    public String uploadFile(MultipartFile file, String entityType, String entityId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > uploadConfig.getMaxFileSize()) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size");
        }

        if (!isImageFile(file)) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadConfig.getDir(), entityType);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file unique
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = entityId + "_" + UUID.randomUUID().toString() + fileExtension;

            // Lưu file
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn relative
            return entityType + "/" + uniqueFileName;

        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new FileUploadException("Failed to upload file", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        try {
            Path fullPath = Paths.get(uploadConfig.getDir(), filePath);
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                log.info("Deleted file: {}", fullPath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", e.getMessage());
        }
    }

    @Override
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        for (String allowedType : uploadConfig.getAllowedImageTypes()) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }
}