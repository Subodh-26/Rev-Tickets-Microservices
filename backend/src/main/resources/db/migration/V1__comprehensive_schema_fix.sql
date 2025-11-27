-- Comprehensive Schema Fix for RevTickets Application
-- This migration ensures all tables are correctly structured and related

-- ============================================================
-- CORE TABLES
-- ============================================================

-- Users table (already exists but ensure structure)
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('USER', 'ADMIN', 'VENUE_MANAGER') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Movies table
CREATE TABLE IF NOT EXISTS movies (
    movie_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    genre VARCHAR(100),
    language VARCHAR(50),
    parental_rating VARCHAR(10),
    release_date DATE,
    cast TEXT,
    crew TEXT,
    trailer_url VARCHAR(500),
    display_image_url VARCHAR(500),
    banner_image_url VARCHAR(500),
    rating DECIMAL(3,1),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_release_date (release_date),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Events table
CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    artist_or_team VARCHAR(255),
    event_date DATE,
    event_time TIME,
    duration_minutes INT,
    age_restriction VARCHAR(10),
    language VARCHAR(50),
    trailer_url VARCHAR(500),
    display_image_url VARCHAR(500),
    banner_image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_date (event_date),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Venues table
CREATE TABLE IF NOT EXISTS venues (
    venue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_name VARCHAR(255) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    total_screens INT,
    facilities JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Screens table
CREATE TABLE IF NOT EXISTS screens (
    screen_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id BIGINT NOT NULL,
    screen_number INT NOT NULL,
    screen_type VARCHAR(50),
    sound_system VARCHAR(100),
    total_seats INT NOT NULL,
    seat_layout JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE,
    UNIQUE KEY uk_venue_screen (venue_id, screen_number),
    INDEX idx_venue (venue_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- SHOW TABLES
-- ============================================================

-- Shows table (for screened events - movies/events)
CREATE TABLE IF NOT EXISTS shows (
    show_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT,
    event_id BIGINT,
    venue_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    pricing_tiers JSON,
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
    INDEX idx_movie_date (movie_id, show_date),
    INDEX idx_event_date (event_id, show_date),
    INDEX idx_show_datetime (show_date, show_time),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Open event shows (for open ground events)
CREATE TABLE IF NOT EXISTS open_event_shows (
    open_show_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    total_capacity INT NOT NULL,
    available_capacity INT NOT NULL,
    pricing_zones JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    INDEX idx_event_date (event_id, show_date),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seats table (individual seats for screened shows)
CREATE TABLE IF NOT EXISTS seats (
    seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_id BIGINT NOT NULL,
    row_label VARCHAR(5) NOT NULL,
    seat_number INT NOT NULL,
    seat_type ENUM('PREMIUM', 'REGULAR', 'ECONOMY') DEFAULT 'REGULAR',
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    UNIQUE KEY uk_show_seat (show_id, row_label, seat_number),
    INDEX idx_show_available (show_id, is_available),
    INDEX idx_show_row (show_id, row_label)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- BOOKING TABLES
-- ============================================================

-- Bookings table (main booking record)
CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    show_id BIGINT,
    open_show_id BIGINT,
    booking_reference VARCHAR(50) UNIQUE NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_seats INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED') DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    razorpay_order_id VARCHAR(100),
    razorpay_payment_id VARCHAR(100),
    razorpay_signature VARCHAR(200),
    qr_code_url VARCHAR(500),
    zone_bookings JSON,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE SET NULL,
    FOREIGN KEY (open_show_id) REFERENCES open_event_shows(open_show_id) ON DELETE SET NULL,
    CHECK ((show_id IS NOT NULL AND open_show_id IS NULL) OR (show_id IS NULL AND open_show_id IS NOT NULL)),
    INDEX idx_user (user_id),
    INDEX idx_booking_ref (booking_reference),
    INDEX idx_show (show_id),
    INDEX idx_open_show (open_show_id),
    INDEX idx_status (booking_status, payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Booking seats table (junction table for booking-seat relationship)
CREATE TABLE IF NOT EXISTS booking_seats (
    booking_seat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    seat_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE CASCADE,
    UNIQUE KEY uk_booking_seat (booking_id, seat_id),
    INDEX idx_booking (booking_id),
    INDEX idx_seat (seat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- ADDITIONAL TABLES
-- ============================================================

-- Cart table (for temporary seat reservations)
CREATE TABLE IF NOT EXISTS cart (
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    show_id BIGINT,
    open_show_id BIGINT,
    seat_ids JSON,
    zone_selections JSON,
    total_amount DECIMAL(10,2),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    FOREIGN KEY (open_show_id) REFERENCES open_event_shows(open_show_id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments table (for payment tracking)
CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    razorpay_order_id VARCHAR(100) NOT NULL,
    razorpay_payment_id VARCHAR(100),
    razorpay_signature VARCHAR(200),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    status ENUM('CREATED', 'AUTHORIZED', 'CAPTURED', 'FAILED', 'REFUNDED') DEFAULT 'CREATED',
    payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_booking (booking_id),
    INDEX idx_razorpay_order (razorpay_order_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- DATA CLEANUP
-- ============================================================

-- Update any existing bookings with incorrect total_amount
-- (This will recalculate based on booking_seats prices)
UPDATE bookings b
SET b.total_amount = (
    SELECT COALESCE(SUM(bs.seat_price), 0)
    FROM booking_seats bs
    WHERE bs.booking_id = b.booking_id
)
WHERE b.show_id IS NOT NULL
AND EXISTS (SELECT 1 FROM booking_seats bs WHERE bs.booking_id = b.booking_id);

-- Mark expired pending bookings as failed
UPDATE bookings
SET booking_status = 'FAILED', payment_status = 'FAILED'
WHERE booking_status = 'PENDING'
AND payment_status = 'PENDING'
AND booking_date < DATE_SUB(NOW(), INTERVAL 15 MINUTE);
