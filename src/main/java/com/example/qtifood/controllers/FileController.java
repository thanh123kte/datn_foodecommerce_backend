package com.example.qtifood.controllers;

import com.example.qtifood.config.UploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final UploadConfig uploadConfig;

    @GetMapping("/{entityType}/{fileName}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String entityType,
            @PathVariable String fileName) {
        
        try {
            Path filePath = Paths.get(uploadConfig.getDir()).resolve(entityType).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Xác định content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("File not found: {}", fileName, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error reading file: {}", fileName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}