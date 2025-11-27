package com.revature.revtickets.controller;

import com.revature.revtickets.dto.MovieDTO;
import com.revature.revtickets.entity.Movie;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies() {
        List<Movie> movies = movieService.getAllActiveMovies();
        return ResponseEntity.ok(new ApiResponse<>(true, "Movies retrieved successfully", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Movie retrieved successfully", movie));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Movie>>> searchMovies(@RequestParam String title) {
        List<Movie> movies = movieService.searchMovies(title);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search results", movies));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponse<List<Movie>>> getMoviesByGenre(@PathVariable String genre) {
        List<Movie> movies = movieService.getMoviesByGenre(genre);
        return ResponseEntity.ok(new ApiResponse<>(true, "Movies by genre retrieved", movies));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<ApiResponse<List<Movie>>> getMoviesByLanguage(@PathVariable String language) {
        List<Movie> movies = movieService.getMoviesByLanguage(language);
        return ResponseEntity.ok(new ApiResponse<>(true, "Movies by language retrieved", movies));
    }

    @GetMapping("/now-showing")
    public ResponseEntity<ApiResponse<List<Movie>>> getNowShowingMovies() {
        List<Movie> movies = movieService.getNowShowingMovies();
        return ResponseEntity.ok(new ApiResponse<>(true, "Now showing movies retrieved", movies));
    }

    @GetMapping("/coming-soon")
    public ResponseEntity<ApiResponse<List<Movie>>> getComingSoonMovies() {
        List<Movie> movies = movieService.getComingSoonMovies();
        return ResponseEntity.ok(new ApiResponse<>(true, "Coming soon movies retrieved", movies));
    }
}
