package com.revature.movieservice.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    @GetMapping("/display/{filename}")
    public ResponseEntity<Resource> getDisplayImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("public/display/" + filename);
            File file = filePath.toFile();
            
            if (file.exists() && file.isFile()) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/banner/{filename}")
    public ResponseEntity<Resource> getBannerImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("public/banner/" + filename);
            File file = filePath.toFile();
            
            if (file.exists() && file.isFile()) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}