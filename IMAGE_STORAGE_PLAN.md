# Image Storage and Retrieval Plan - RevTickets

## Overview
This document explains how movie images (Display Image/Poster and Banner Image) are uploaded, stored, and retrieved in the RevTickets application.

---

## 1. Frontend Upload Flow

### Movie Form Component
**Location:** `frontend/src/app/features/admin/movies/movie-form.component.ts`

#### File Selection
```typescript
// User selects files via file input
onPosterFileChange(event: any) {
  const file = event.target.files[0];
  if (file) {
    this.posterFile = file;
    // Create preview
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.posterPreview = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}

onBannerFileChange(event: any) {
  const file = event.target.files[0];
  if (file) {
    this.bannerFile = file;
    // Create preview
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.bannerPreview = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}
```

#### Form Submission
```typescript
onSubmit() {
  if (this.movieForm.invalid) return;

  // Create FormData for multipart upload
  const formData = new FormData();
  
  // Append movie data as JSON blob
  const movieData = {
    title: this.movieForm.value.title,
    description: this.movieForm.value.description,
    crew: this.movieForm.value.crew,
    genre: this.movieForm.value.genre,
    duration: this.movieForm.value.duration,
    rating: this.movieForm.value.rating,
    language: this.movieForm.value.language,
    releaseDate: this.movieForm.value.releaseDate,
    cast: this.movieForm.value.cast || '',
    trailerUrl: this.movieForm.value.trailerUrl || ''
  };
  
  formData.append('movie', new Blob([JSON.stringify(movieData)], { 
    type: 'application/json' 
  }));
  
  // Append image files
  if (this.posterFile) {
    formData.append('displayImage', this.posterFile);
  }
  if (this.bannerFile) {
    formData.append('bannerImage', this.bannerFile);
  }
  
  // Send to backend
  this.http.post('/api/admin/movies', formData).subscribe(...);
}
```

---

## 2. Backend Storage Flow

### Controller Layer
**Location:** `backend/src/main/java/com/revature/revtickets/controller/admin/AdminMovieController.java`

```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Movie> createMovie(
    @RequestPart("movie") MovieDTO movieDTO,
    @RequestPart("displayImage") MultipartFile displayImage,
    @RequestPart("bannerImage") MultipartFile bannerImage
) {
    Movie savedMovie = movieService.createMovie(movieDTO, displayImage, bannerImage);
    return ResponseEntity.ok(savedMovie);
}
```

### Service Layer
**Location:** `backend/src/main/java/com/revature/revtickets/service/MovieService.java`

**Expected Implementation:**
```java
@Service
public class MovieService {
    
    @Value("${file.upload.dir}")
    private String uploadDir; // "public/images"
    
    public Movie createMovie(MovieDTO dto, MultipartFile displayImage, MultipartFile bannerImage) {
        // 1. Save Display Image (Poster)
        String displayImageUrl = saveFile(displayImage, "display");
        
        // 2. Save Banner Image
        String bannerImageUrl = saveFile(bannerImage, "banner");
        
        // 3. Create Movie entity
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setCrew(dto.getCrew());
        movie.setGenre(dto.getGenre());
        movie.setDuration(dto.getDuration());
        movie.setRating(dto.getRating());
        movie.setLanguage(dto.getLanguage());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setCast(dto.getCast());
        movie.setTrailerUrl(dto.getTrailerUrl());
        
        // Set image URLs
        movie.setDisplayImageUrl(displayImageUrl);
        movie.setBannerImageUrl(bannerImageUrl);
        
        // 4. Save to database
        return movieRepository.save(movie);
    }
    
    private String saveFile(MultipartFile file, String type) throws IOException {
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = type + "_" + UUID.randomUUID().toString() + extension;
        
        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return URL path
        return "/images/" + uniqueFilename;
    }
}
```

### File Storage Location
- **Physical Path:** `backend/public/images/`
- **File Naming:** `{type}_{UUID}.{extension}`
  - Example: `display_a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg`
  - Example: `banner_f6e5d4c3-b2a1-0987-6543-210fedcba987.png`

---

## 3. Database Schema

### Movie Entity
**Location:** `backend/src/main/java/com/revature/revtickets/entity/Movie.java`

**Expected Fields:**
```java
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    private String crew; // Comma-separated: "Director, Writer, etc."
    
    private String genre;
    private Integer duration; // in minutes
    private String rating; // PG, PG-13, R, etc.
    private String language;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(length = 1000)
    private String cast; // Comma-separated actor names
    
    @Column(name = "trailer_url")
    private String trailerUrl;
    
    // IMAGE FIELDS
    @Column(name = "display_image_url")
    private String displayImageUrl; // URL: /images/display_{uuid}.jpg
    
    @Column(name = "banner_image_url")
    private String bannerImageUrl; // URL: /images/banner_{uuid}.jpg
    
    // Getters and setters
}
```

### Database Table
```sql
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    crew VARCHAR(500),
    genre VARCHAR(100),
    duration INT,
    rating VARCHAR(20),
    language VARCHAR(50),
    release_date DATE,
    cast VARCHAR(1000),
    trailer_url VARCHAR(500),
    display_image_url VARCHAR(500), -- Stores: /images/display_{uuid}.jpg
    banner_image_url VARCHAR(500),  -- Stores: /images/banner_{uuid}.jpg
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 4. Static File Serving

### Spring Boot Configuration
**Location:** `backend/src/main/resources/application.properties`

```properties
# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload.dir=public/images

# Static Resource Serving (if needed)
spring.web.resources.static-locations=classpath:/static/,file:public/
```

### WebConfig (Optional - for custom static serving)
**Location:** `backend/src/main/java/com/revature/revtickets/config/WebConfig.java`

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
```

**This makes images accessible at:** `http://localhost:8080/images/display_a1b2c3d4.jpg`

---

## 5. Retrieval Flow

### Backend Response
When frontend requests movie data:

**GET /api/admin/movies** returns:
```json
[
  {
    "id": 1,
    "title": "RRR",
    "description": "Epic period action film...",
    "crew": "SS Rajamouli, V. Vijayendra Prasad",
    "genre": "Action, Drama",
    "duration": 187,
    "rating": "PG-13",
    "language": "Telugu",
    "releaseDate": "2022-03-25",
    "cast": "Ram Charan, Jr NTR, Alia Bhatt",
    "trailerUrl": "https://www.youtube.com/watch?v=...",
    "displayImageUrl": "/images/display_a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
    "bannerImageUrl": "/images/banner_f6e5d4c3-b2a1-0987-6543-210fedcba987.png"
  }
]
```

### Frontend Display

#### Movie List Component
**Location:** `frontend/src/app/features/admin/movies/movie-list.component.ts`

**Update Template to Display Images:**
```html
<table class="w-full">
  <thead>
    <tr>
      <th>Poster</th>
      <th>Title</th>
      <th>Crew</th>
      <th>Genre</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let movie of movies">
      <td>
        <img 
          [src]="'http://localhost:8080' + movie.displayImageUrl" 
          alt="{{movie.title}}"
          class="w-16 h-24 object-cover rounded-lg"
        />
      </td>
      <td>{{ movie.title }}</td>
      <td>{{ movie.crew }}</td>
      <td>{{ movie.genre }}</td>
      <td>
        <button (click)="editMovie(movie.id)">Edit</button>
        <button (click)="deleteMovie(movie.id)">Delete</button>
      </td>
    </tr>
  </tbody>
</table>
```

#### User-Facing Movie Cards
**For displaying movies to end users:**
```html
<!-- Movie Card -->
<div class="movie-card">
  <img 
    [src]="'http://localhost:8080' + movie.displayImageUrl" 
    alt="{{movie.title}}"
    class="w-full h-96 object-cover rounded-lg"
  />
  <h3>{{ movie.title }}</h3>
  <p>{{ movie.description }}</p>
</div>

<!-- Banner Image (for homepage slider) -->
<div class="banner">
  <img 
    [src]="'http://localhost:8080' + movie.bannerImageUrl" 
    alt="{{movie.title}}"
    class="w-full h-auto"
  />
</div>
```

---

## 6. Complete Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. USER SELECTS FILES                                           │
│    Frontend: movie-form.component.ts                            │
│    - User clicks "Choose File" for Display Image (poster)       │
│    - User clicks "Choose File" for Banner Image                 │
│    - Preview shown using FileReader                             │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. FORM SUBMISSION                                              │
│    - Create FormData with:                                      │
│      * movie JSON blob                                          │
│      * displayImage file                                        │
│      * bannerImage file                                         │
│    - POST to /api/admin/movies                                  │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. BACKEND RECEIVES REQUEST                                     │
│    Controller: AdminMovieController.java                        │
│    - @RequestPart("movie") → MovieDTO                           │
│    - @RequestPart("displayImage") → MultipartFile               │
│    - @RequestPart("bannerImage") → MultipartFile                │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. FILE STORAGE                                                 │
│    Service: MovieService.java                                   │
│    - Generate unique filename: display_{UUID}.jpg               │
│    - Save to: backend/public/images/                            │
│    - Return URL: /images/display_{UUID}.jpg                     │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. DATABASE SAVE                                                │
│    Repository: MovieRepository.java                             │
│    - Movie entity with:                                         │
│      * displayImageUrl = "/images/display_{UUID}.jpg"           │
│      * bannerImageUrl = "/images/banner_{UUID}.jpg"             │
│    - Save to MySQL movies table                                 │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 6. RESPONSE TO FRONTEND                                         │
│    - Return saved Movie object as JSON                          │
│    - Frontend receives movie with image URLs                    │
│    - Redirect to movie list                                     │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│ 7. DISPLAY IMAGES                                               │
│    Movie List: movie-list.component.ts                          │
│    - GET /api/admin/movies                                      │
│    - Receive array with displayImageUrl, bannerImageUrl         │
│    - Display: <img [src]="'http://localhost:8080' +             │
│                movie.displayImageUrl">                          │
│                                                                  │
│    Static File Serving:                                         │
│    - Request: http://localhost:8080/images/display_{UUID}.jpg   │
│    - Spring serves from: backend/public/images/                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 7. Image Types & Purposes

### Display Image (Poster)
- **Aspect Ratio:** 9:16 (vertical)
- **Recommended Size:** 600x900 px or 900x1350 px
- **Purpose:** Movie cards, thumbnails, search results
- **Used In:**
  - Movie list table (small thumbnail)
  - User-facing movie cards
  - Search results
  - "Now Showing" sections

### Banner Image
- **Aspect Ratio:** 16:9 (horizontal)
- **Recommended Size:** 1920x1080 px
- **Purpose:** Homepage banners, featured movies slider
- **Used In:**
  - Homepage hero carousel
  - Movie detail page header
  - Featured/promoted movies

---

## 8. Error Handling

### Frontend Validation
```typescript
// In movie-form.component.ts
onSubmit() {
  // Check if files are selected (required for create)
  if (!this.movieId && !this.posterFile) {
    alert('Display Image is required');
    return;
  }
  
  if (!this.movieId && !this.bannerFile) {
    alert('Banner Image is required');
    return;
  }
  
  // Check file size (max 10MB)
  if (this.posterFile && this.posterFile.size > 10 * 1024 * 1024) {
    alert('Display Image must be less than 10MB');
    return;
  }
  
  if (this.bannerFile && this.bannerFile.size > 10 * 1024 * 1024) {
    alert('Banner Image must be less than 10MB');
    return;
  }
  
  // Check file type
  const validTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
  if (this.posterFile && !validTypes.includes(this.posterFile.type)) {
    alert('Display Image must be JPG, PNG, or WebP');
    return;
  }
}
```

### Backend Validation
```java
// In MovieService.java
private String saveFile(MultipartFile file, String type) throws IOException {
    // Check if file is empty
    if (file.isEmpty()) {
        throw new IllegalArgumentException("File is empty");
    }
    
    // Validate file type
    String contentType = file.getContentType();
    if (!contentType.startsWith("image/")) {
        throw new IllegalArgumentException("File must be an image");
    }
    
    // Validate file size (10MB)
    if (file.getSize() > 10 * 1024 * 1024) {
        throw new IllegalArgumentException("File size exceeds 10MB");
    }
    
    // Save file...
}
```

---

## 9. Next Steps

### Immediate Actions Required:

1. **Update Movie Entity**
   - Add `displayImageUrl` and `bannerImageUrl` fields
   - Generate database migration

2. **Implement MovieService File Handling**
   - Create `saveFile()` method
   - Update `createMovie()` and `updateMovie()` methods
   - Handle file deletion on movie update/delete

3. **Configure Static File Serving**
   - Add WebConfig to serve files from `public/images`
   - Test image access at `http://localhost:8080/images/{filename}`

4. **Update Movie List Component**
   - Display poster thumbnail in table
   - Update image source to use backend URL

5. **Test Complete Flow**
   - Upload movie with images
   - Verify files saved to `backend/public/images/`
   - Verify database has image URLs
   - Verify images display in movie list

---

## 10. File Structure Reference

```
revtickets_new/
├── backend/
│   ├── src/main/java/com/revature/revtickets/
│   │   ├── controller/admin/
│   │   │   └── AdminMovieController.java     ← Handles multipart upload
│   │   ├── service/
│   │   │   └── MovieService.java              ← Saves files, returns URLs
│   │   ├── entity/
│   │   │   └── Movie.java                     ← Has displayImageUrl, bannerImageUrl
│   │   ├── dto/
│   │   │   └── MovieDTO.java                  ← Movie form data
│   │   └── config/
│   │       └── WebConfig.java                 ← Static file serving
│   ├── src/main/resources/
│   │   └── application.properties             ← Upload directory config
│   └── public/images/                         ← Image storage directory
│       ├── display_a1b2c3d4...jpg            ← Poster files
│       └── banner_f6e5d4c3...png             ← Banner files
│
└── frontend/src/app/features/admin/movies/
    ├── movie-form.component.ts                ← File upload form
    └── movie-list.component.ts                ← Displays uploaded images
```

---

## Summary

**Storage:** Files saved to `backend/public/images/` with unique names  
**Database:** URLs stored as `/images/{filename}` in Movie entity  
**Retrieval:** Spring serves files from `public/images/` at `/images/**` route  
**Display:** Frontend uses `http://localhost:8080/images/{filename}` in `<img>` tags

