package com.revature.revtickets.controller;

import com.revature.revtickets.entity.Banner;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Banner>>> getAllBanners() {
        List<Banner> banners = bannerService.getAllActiveBanners();
        return ResponseEntity.ok(new ApiResponse<>(true, "Banners retrieved successfully", banners));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Banner>> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Banner retrieved successfully", banner));
    }
}
