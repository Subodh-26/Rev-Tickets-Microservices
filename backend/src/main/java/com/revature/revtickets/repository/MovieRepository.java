package com.revature.revtickets.repository;

import com.revature.revtickets.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByIsActiveTrue();
    List<Movie> findByGenreContainingIgnoreCaseAndIsActiveTrue(String genre);
    List<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
    List<Movie> findByLanguageAndIsActiveTrue(String language);
    
    @Query("SELECT m FROM Movie m WHERE m.releaseDate <= CURRENT_DATE AND m.isActive = true")
    List<Movie> findNowShowingMovies();
    
    @Query("SELECT m FROM Movie m WHERE m.releaseDate > CURRENT_DATE AND m.isActive = true")
    List<Movie> findComingSoonMovies();
}
