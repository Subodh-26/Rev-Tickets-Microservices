-- RevTickets Database Schema
-- Drop database if exists and create fresh
DROP DATABASE IF EXISTS revticketsnew;
CREATE DATABASE revticketsnew;
USE revticketsnew;

-- Users table with role-based access
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'ADMIN') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Venues/Theaters table
CREATE TABLE venues (
    venue_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    venue_name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    pincode VARCHAR(10),
    total_screens INT DEFAULT 1,
    facilities JSON, -- parking, food_court, wheelchair_access, etc.
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city)
);

-- Screens table (individual screens within venues)
CREATE TABLE screens (
    screen_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    venue_id BIGINT NOT NULL,
    screen_number INT NOT NULL,
    screen_type VARCHAR(50), -- 2D, 3D, IMAX, 4DX
    sound_system VARCHAR(100), -- Dolby Atmos, DTS, Standard
    seat_layout JSON NOT NULL, -- Seat configuration for this screen
    total_seats INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE,
    INDEX idx_venue_screen (venue_id, screen_number)
);

-- Seat Layout Configuration (Reusable templates)
CREATE TABLE seat_layouts (
    layout_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    layout_name VARCHAR(255) NOT NULL,
    total_rows INT NOT NULL,
    total_columns INT NOT NULL,
    seat_configuration JSON NOT NULL, -- {row: A, seats: [{col: 1, "type": REGULAR, available: true}]}
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Movies table
CREATE TABLE movies (
    movie_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    genre VARCHAR(255), -- Action, Drama, Comedy, etc.
    language VARCHAR(100),
    parental_rating VARCHAR(10), -- U, U/A, A, R, PG-13, etc.
    release_date DATE,
    cast TEXT, -- JSON array of actor names
    crew TEXT, -- JSON array of {role, name}
    trailer_url VARCHAR(500),
    display_image_url VARCHAR(500), -- poster
    banner_image_url VARCHAR(500), -- hero banner
    rating DECIMAL(2,1) DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_genre (genre),
    INDEX idx_release_date (release_date)
);

-- Events table (concerts, shows, sports, etc.)
CREATE TABLE events (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100), -- Concert, Sports, Theatre, Comedy, etc.
    event_date DATE NOT NULL,
    event_time TIME,
    duration_minutes INT,
    artist_or_team VARCHAR(255),
    language VARCHAR(100),
    age_restriction VARCHAR(50),
    display_image_url VARCHAR(500),
    banner_image_url VARCHAR(500),
    trailer_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_category (category),
    INDEX idx_event_date (event_date)
);

-- Shows table (specific showtime for movies/events at venues)
CREATE TABLE shows (
    show_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT,
    event_id BIGINT,
    venue_id BIGINT,
    screen_id BIGINT,
    is_open_ground BOOLEAN DEFAULT FALSE,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    base_price DECIMAL(10,2) NOT NULL, -- Price in INR
    pricing_tiers JSON, -- {PREMIUM: 350, REGULAR: 250, ECONOMY: 150} or {VIP: 1000, Gold: 500, Silver: 300}
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE,
    FOREIGN KEY (screen_id) REFERENCES screens(screen_id) ON DELETE CASCADE,
    CHECK ((movie_id IS NOT NULL AND event_id IS NULL) OR (movie_id IS NULL AND event_id IS NOT NULL)),
    CHECK ((is_open_ground = FALSE AND venue_id IS NOT NULL AND screen_id IS NOT NULL) OR (is_open_ground = TRUE AND venue_id IS NULL AND screen_id IS NULL)),
    INDEX idx_show_date_time (show_date, show_time),
    INDEX idx_venue (venue_id),
    INDEX idx_movie (movie_id),
    INDEX idx_event (event_id),
    INDEX idx_screen (screen_id)
);

-- Seats table (actual seats for each show)
CREATE TABLE seats (
    seat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    show_id BIGINT NOT NULL,
    row_label VARCHAR(5) NOT NULL, -- A, B, C, etc.
    seat_number INT NOT NULL,
    seat_type ENUM('PREMIUM', 'REGULAR', 'ECONOMY', 'RECLINER', 'VIP') DEFAULT 'REGULAR',
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE, -- For handicap, maintenance, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    UNIQUE KEY unique_seat_per_show (show_id, row_label, seat_number),
    INDEX idx_show_availability (show_id, is_available)
);

-- Bookings table
CREATE TABLE bookings (
    booking_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    booking_reference VARCHAR(50) UNIQUE NOT NULL, -- BK20250123ABC123
    total_seats INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    qr_code_url VARCHAR(500), -- Path to generated QR code
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (show_id) REFERENCES shows(show_id),
    INDEX idx_user (user_id),
    INDEX idx_booking_reference (booking_reference),
    INDEX idx_status (booking_status)
);

-- Booking Details (seats booked)
CREATE TABLE booking_seats (
    booking_seat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    seat_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id),
    UNIQUE KEY unique_booking_seat (booking_id, seat_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    payment_intent_id VARCHAR(255), -- Stripe payment intent ID
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    payment_method ENUM('CARD', 'UPI', 'NETBANKING', 'WALLET') NOT NULL,
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(255),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    INDEX idx_booking (booking_id),
    INDEX idx_payment_intent (payment_intent_id),
    INDEX idx_status (payment_status)
);

-- Cart table (temporary storage before booking)
CREATE TABLE cart (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP, -- 10 minutes hold
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_seat (user_id, seat_id),
    INDEX idx_user_show (user_id, show_id),
    INDEX idx_expires (expires_at)
);

-- Banners for homepage (sliding carousel)
CREATE TABLE banners (
    banner_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    movie_id BIGINT,
    event_id BIGINT,
    banner_image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    link_url VARCHAR(500), -- Optional external link
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    INDEX idx_display_order (display_order),
    INDEX idx_active (is_active)
);

-- Insert default admin user
-- Password: admin123 (bcrypt hashed)
INSERT INTO users (email, password, full_name, phone, role) VALUES
('admin@revtickets.com', '$2a$10$dXJ3SW6G7P7mh3ZXFNRnzOEPXjdYqLPf8RqxD2Jlko8j1eBQOd5Oi', 'Admin User', '9999999999', 'ADMIN');

-- Sample venue
INSERT INTO venues (venue_name, address, city, state, pincode, total_screens, facilities) VALUES
('PVR Phoenix Mall', 'Viman Nagar, Pune', 'Pune', 'Maharashtra', '411014', 5, '{"parking": true, "food_court": true, "wheelchair_access": true}'),
('INOX Bund Garden', 'Bund Garden Road, Pune', 'Pune', 'Maharashtra', '411001', 4, '{"parking": true, "food_court": true, "3d_screen": true}'),
('Cinepolis Seasons Mall', 'Magarpatta, Pune', 'Pune', 'Maharashtra', '411028', 6, '{"parking": true, "food_court": true, "imax": true}');

-- Sample seat layout
INSERT INTO seat_layouts (layout_name, total_rows, total_columns, seat_configuration) VALUES
('Standard Hall - 100 Seats', 10, 10, 
'[
  {"row": "A", "seats": [{"col": 1, "type": "PREMIUM"}, {"col": 2, "type": "PREMIUM"}, {"col": 3, "type": "PREMIUM"}, {"col": 4, "type": "PREMIUM"}, {"col": 5, "type": "PREMIUM"}, {"col": 6, "type": "PREMIUM"}, {"col": 7, "type": "PREMIUM"}, {"col": 8, "type": "PREMIUM"}, {"col": 9, "type": "PREMIUM"}, {"col": 10, "type": "PREMIUM"}]},
  {"row": "B", "seats": [{"col": 1, "type": "PREMIUM"}, {"col": 2, "type": "PREMIUM"}, {"col": 3, "type": "PREMIUM"}, {"col": 4, "type": "PREMIUM"}, {"col": 5, "type": "PREMIUM"}, {"col": 6, "type": "PREMIUM"}, {"col": 7, "type": "PREMIUM"}, {"col": 8, "type": "PREMIUM"}, {"col": 9, "type": "PREMIUM"}, {"col": 10, "type": "PREMIUM"}]},
  {"row": "C", "seats": [{"col": 1, "type": "REGULAR"}, {"col": 2, "type": "REGULAR"}, {"col": 3, "type": "REGULAR"}, {"col": 4, "type": "REGULAR"}, {"col": 5, "type": "REGULAR"}, {"col": 6, "type": "REGULAR"}, {"col": 7, "type": "REGULAR"}, {"col": 8, "type": "REGULAR"}, {"col": 9, "type": "REGULAR"}, {"col": 10, "type": "REGULAR"}]},
  {"row": "D", "seats": [{"col": 1, "type": "REGULAR"}, {"col": 2, "type": "REGULAR"}, {"col": 3, "type": "REGULAR"}, {"col": 4, "type": "REGULAR"}, {"col": 5, "type": "REGULAR"}, {"col": 6, "type": "REGULAR"}, {"col": 7, "type": "REGULAR"}, {"col": 8, "type": "REGULAR"}, {"col": 9, "type": "REGULAR"}, {"col": 10, "type": "REGULAR"}]},
  {"row": "E", "seats": [{"col": 1, "type": "REGULAR"}, {"col": 2, "type": "REGULAR"}, {"col": 3, "type": "REGULAR"}, {"col": 4, "type": "REGULAR"}, {"col": 5, "type": "REGULAR"}, {"col": 6, "type": "REGULAR"}, {"col": 7, "type": "REGULAR"}, {"col": 8, "type": "REGULAR"}, {"col": 9, "type": "REGULAR"}, {"col": 10, "type": "REGULAR"}]},
  {"row": "F", "seats": [{"col": 1, "type": "REGULAR"}, {"col": 2, "type": "REGULAR"}, {"col": 3, "type": "REGULAR"}, {"col": 4, "type": "REGULAR"}, {"col": 5, "type": "REGULAR"}, {"col": 6, "type": "REGULAR"}, {"col": 7, "type": "REGULAR"}, {"col": 8, "type": "REGULAR"}, {"col": 9, "type": "REGULAR"}, {"col": 10, "type": "REGULAR"}]},
  {"row": "G", "seats": [{"col": 1, "type": "REGULAR"}, {"col": 2, "type": "REGULAR"}, {"col": 3, "type": "REGULAR"}, {"col": 4, "type": "REGULAR"}, {"col": 5, "type": "REGULAR"}, {"col": 6, "type": "REGULAR"}, {"col": 7, "type": "REGULAR"}, {"col": 8, "type": "REGULAR"}, {"col": 9, "type": "REGULAR"}, {"col": 10, "type": "REGULAR"}]},
  {"row": "H", "seats": [{"col": 1, "type": "ECONOMY"}, {"col": 2, "type": "ECONOMY"}, {"col": 3, "type": "ECONOMY"}, {"col": 4, "type": "ECONOMY"}, {"col": 5, "type": "ECONOMY"}, {"col": 6, "type": "ECONOMY"}, {"col": 7, "type": "ECONOMY"}, {"col": 8, "type": "ECONOMY"}, {"col": 9, "type": "ECONOMY"}, {"col": 10, "type": "ECONOMY"}]},
  {"row": "I", "seats": [{"col": 1, "type": "ECONOMY"}, {"col": 2, "type": "ECONOMY"}, {"col": 3, "type": "ECONOMY"}, {"col": 4, "type": "ECONOMY"}, {"col": 5, "type": "ECONOMY"}, {"col": 6, "type": "ECONOMY"}, {"col": 7, "type": "ECONOMY"}, {"col": 8, "type": "ECONOMY"}, {"col": 9, "type": "ECONOMY"}, {"col": 10, "type": "ECONOMY"}]},
  {"row": "J", "seats": [{"col": 1, "type": "ECONOMY"}, {"col": 2, "type": "ECONOMY"}, {"col": 3, "type": "ECONOMY"}, {"col": 4, "type": "ECONOMY"}, {"col": 5, "type": "ECONOMY"}, {"col": 6, "type": "ECONOMY"}, {"col": 7, "type": "ECONOMY"}, {"col": 8, "type": "ECONOMY"}, {"col": 9, "type": "ECONOMY"}, {"col": 10, "type": "ECONOMY"}]}
]');

-- MongoDB Collections (to be created in application)
-- Collections:
-- 1. reviews: {reviewId, userId, movieId/eventId, rating, comment, likes, createdAt}
-- 2. notifications: {notificationId, userId, type, title, message, isRead, createdAt}
-- 3. activity_logs: {logId, userId, action, details, timestamp}
