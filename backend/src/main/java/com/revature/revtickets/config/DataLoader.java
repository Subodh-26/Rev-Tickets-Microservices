package com.revature.revtickets.config;

import com.revature.revtickets.entity.User;
import com.revature.revtickets.entity.Movie;
import com.revature.revtickets.repository.UserRepository;
import com.revature.revtickets.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists, create only if it doesn't
        if (!userRepository.findByEmail("admin@revtickets.com").isPresent()) {
            // Create fresh admin user
            User admin = new User();
            admin.setEmail("admin@revtickets.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setPhone("1234567890");
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsActive(true);
            
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin@revtickets.com / admin123");
            System.out.println("📧 Email: admin@revtickets.com");
            System.out.println("🔑 Password: admin123");
        } else {
            System.out.println("ℹ️ Admin user already exists: admin@revtickets.com");
        }

        // Check and log existing movies
        long movieCount = movieRepository.count();
        System.out.println("📊 Current movies in database: " + movieCount);
        
        // Check if there are active movies with valid release dates
        List<Movie> allMovies = movieRepository.findByIsActiveTrue();
        List<Movie> nowShowing = movieRepository.findNowShowingMovies();
        System.out.println("🎬 Now showing movies count: " + nowShowing.size());
        
        if (nowShowing.isEmpty() && !allMovies.isEmpty()) {
            System.out.println("⚠️ Found " + allMovies.size() + " movies with future release dates!");
            System.out.println("🔧 Updating release dates to today for display...");
            
            for (Movie movie : allMovies) {
                System.out.println("   Updating: " + movie.getTitle() + " (was: " + movie.getReleaseDate() + ")");
                movie.setReleaseDate(LocalDate.now().minusDays(1)); // Set to yesterday to ensure it shows
                movieRepository.save(movie);
            }
            
            System.out.println("✅ Updated " + allMovies.size() + " movies to show in 'Now Showing'");
        } else if (nowShowing.isEmpty() && movieCount == 0) {
            System.out.println("📝 Creating sample movies...");
            createSampleMovie("Varanasi", "Action/Thriller", "Telugu", 183, 
                "A gripping tale of mystery and action in the holy city", 
                LocalDate.now().minusDays(10), "U/A", 8.5,
                "/movies/5bae239b-0ce6-42dd-9901-cfacd204855c.jfif",
                "/banners/5bae239b-0ce6-42dd-9901-cfacd204855c.jfif");
            
            createSampleMovie("The Matrix", "Sci-Fi/Action", "English", 136, 
                "A computer hacker learns about the true nature of reality", 
                LocalDate.now().minusDays(30), "PG-13", 8.7,
                "/movies/a8bb837a-9ef9-4cb4-a4a8-8659c4061f93.svg",
                "/banners/a8bb837a-9ef9-4cb4-a4a8-8659c4061f93.svg");
            
            System.out.println("✅ Sample movies created");
        } else {
            System.out.println("✅ Movies available for display:");
            nowShowing.forEach(m -> System.out.println("   - " + m.getTitle() + " (Released: " + m.getReleaseDate() + ")"));
        }
    }

    private void createSampleMovie(String title, String genre, String language, int duration,
                                   String description, LocalDate releaseDate, String rating,
                                   double movieRating, String displayImage, String bannerImage) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setLanguage(language);
        movie.setDurationMinutes(duration);
        movie.setDescription(description);
        movie.setReleaseDate(releaseDate);
        movie.setParentalRating(rating);
        movie.setRating(BigDecimal.valueOf(movieRating));
        movie.setDisplayImageUrl(displayImage);
        movie.setBannerImageUrl(bannerImage);
        movie.setIsActive(true);
        movie.setCast("Sample Cast");
        movie.setCrew("Sample Crew");
        
        movieRepository.save(movie);
    }
}
