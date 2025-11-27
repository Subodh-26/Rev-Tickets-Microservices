package com.revature.revtickets.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${file.upload.dir:public/images}")
    private String uploadDir;

    public String uploadFile(MultipartFile file, String subdirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        Path uploadPath = Paths.get(uploadDir, subdirectory);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + subdirectory + "/" + newFilename;
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String sanitizedPath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path filePath = Paths.get(uploadDir).resolve(sanitizedPath);
            Files.deleteIfExists(filePath);
        }
    }
}
