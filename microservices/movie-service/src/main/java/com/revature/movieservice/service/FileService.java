package com.revature.movieservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private static final String DISPLAY_IMAGE_DIRECTORY = "public/display/";
    private static final String BANNER_IMAGE_DIRECTORY = "public/banner/";

    public String saveImage(MultipartFile file, String directory) throws IOException {
        // Create directory if it doesn't exist
        Path dirPath = Paths.get(directory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return filename;
    }

    public String saveDisplayImage(MultipartFile file) throws IOException {
        return saveImage(file, DISPLAY_IMAGE_DIRECTORY);
    }

    public String saveBannerImage(MultipartFile file) throws IOException {
        return saveImage(file, BANNER_IMAGE_DIRECTORY);
    }

    public org.springframework.core.io.Resource loadDisplayImage(String filename) throws IOException {
        Path filePath = Paths.get(DISPLAY_IMAGE_DIRECTORY).resolve(filename);
        org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("File not found: " + filename);
        }
    }

    public org.springframework.core.io.Resource loadBannerImage(String filename) throws IOException {
        Path filePath = Paths.get(BANNER_IMAGE_DIRECTORY).resolve(filename);
        org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("File not found: " + filename);
        }
    }
}