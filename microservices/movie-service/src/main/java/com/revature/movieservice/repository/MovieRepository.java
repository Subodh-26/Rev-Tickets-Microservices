package com.revature.movieservice.repository;

import com.revature.movieservice.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByIsActiveTrue();
    List<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
    List<Movie> findByGenreContainingIgnoreCaseAndIsActiveTrue(String genre);
    List<Movie> findByLanguageAndIsActiveTrue(String language);
}
