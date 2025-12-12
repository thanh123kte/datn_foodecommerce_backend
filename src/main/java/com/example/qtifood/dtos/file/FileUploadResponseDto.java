package com.example.qtifood.dtos.file;

public record FileUploadResponseDto(
    String fileName,
    String filePath,
    String fileUrl,
    long fileSize,
    String contentType
) {
}