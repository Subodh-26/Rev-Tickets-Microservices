package com.revature.revtickets.controller;

import com.revature.revtickets.entity.Movie;
import com.revature.revtickets.repository.MovieRepository;
import com.revature.revtickets.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/movies")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminMovieController {

    @Autowired
    private MovieRepository movieRepository;

    private static final String DISPLAY_IMAGE_DIRECTORY = "backend/public/display/";
    private static final String BANNER_IMAGE_DIRECTORY = "backend/public/banner/";

    @GetMapping
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies(@RequestParam(defaultValue = "true") Boolean activeOnly) {
        try {
            List<Movie> movies = activeOnly ? 
                movieRepository.findByIsActiveTrue() : 
                movieRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Movies fetched successfully", movies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch movies: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable Long id) {
        try {
            Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie fetched successfully", movie));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch movie: " + e.getMessage(), null));
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Movie>> createMovie(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String genre,
            @RequestParam String language,
            @RequestParam Integer duration,
            @RequestParam String releaseDate,
            @RequestParam String parentalRating,
            @RequestParam(required = false) String cast,
            @RequestParam(required = false) String crew,
            @RequestParam(required = false) String trailerUrl,
            @RequestParam(required = false) MultipartFile displayImage,
            @RequestParam(required = false) MultipartFile bannerImage) {
        try {
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setGenre(genre);
            movie.setLanguage(language);
            movie.setDurationMinutes(duration);
            movie.setReleaseDate(LocalDate.parse(releaseDate));
            movie.setParentalRating(parentalRating);
            movie.setCast(cast);
            movie.setCrew(crew);
            movie.setTrailerUrl(trailerUrl);
            movie.setRating(BigDecimal.valueOf(0.0));
            movie.setIsActive(true);

            if (displayImage != null && !displayImage.isEmpty()) {
                String filename = saveImage(displayImage, DISPLAY_IMAGE_DIRECTORY);
                movie.setDisplayImageUrl("/display/" + filename);
            }

            if (bannerImage != null && !bannerImage.isEmpty()) {
                String filename = saveImage(bannerImage, BANNER_IMAGE_DIRECTORY);
                movie.setBannerImageUrl("/banner/" + filename);
            }

            Movie savedMovie = movieRepository.save(movie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie created successfully", savedMovie));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create movie: " + e.getMessage(), null));
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Movie>> updateMovie(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String genre,
            @RequestParam String language,
            @RequestParam Integer duration,
            @RequestParam String releaseDate,
            @RequestParam String parentalRating,
            @RequestParam(required = false) String cast,
            @RequestParam(required = false) String crew,
            @RequestParam(required = false) String trailerUrl,
            @RequestParam(required = false) MultipartFile displayImage,
            @RequestParam(required = false) MultipartFile bannerImage) {
        try {
            Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
            
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setGenre(genre);
            movie.setLanguage(language);
            movie.setDurationMinutes(duration);
            movie.setReleaseDate(LocalDate.parse(releaseDate));
            movie.setParentalRating(parentalRating);
            movie.setCast(cast);
            movie.setCrew(crew);
            movie.setTrailerUrl(trailerUrl);

            if (displayImage != null && !displayImage.isEmpty()) {
                if (movie.getDisplayImageUrl() != null) {
                    deleteImage(movie.getDisplayImageUrl(), DISPLAY_IMAGE_DIRECTORY);
                }
                String filename = saveImage(displayImage, DISPLAY_IMAGE_DIRECTORY);
                movie.setDisplayImageUrl("/display/" + filename);
            }

            if (bannerImage != null && !bannerImage.isEmpty()) {
                if (movie.getBannerImageUrl() != null) {
                    deleteImage(movie.getBannerImageUrl(), BANNER_IMAGE_DIRECTORY);
                }
                String filename = saveImage(bannerImage, BANNER_IMAGE_DIRECTORY);
                movie.setBannerImageUrl("/banner/" + filename);
            }

            Movie savedMovie = movieRepository.save(movie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie updated successfully", savedMovie));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to update movie: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        try {
            Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
            
            // Soft delete - just set isActive to false
            movie.setIsActive(false);
            movieRepository.save(movie);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to deactivate movie: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Movie>> activateMovie(@PathVariable Long id) {
        try {
            Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
            
            movie.setIsActive(true);
            Movie savedMovie = movieRepository.save(movie);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Movie activated successfully", savedMovie));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to activate movie: " + e.getMessage(), null));
        }
    }

    private String saveImage(MultipartFile file, String directory) throws IOException {
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(directory);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        return filename;
    }

    private void deleteImage(String imagePath, String directory) {
        try {
            // Extract filename from path like "/display/filename.jpg" or "/banner/filename.jpg"
            String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            Path filePath = Paths.get(directory + filename);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }
}
