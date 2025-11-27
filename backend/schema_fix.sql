-- Run this script in MySQL to apply comprehensive schema fixes
-- Database: revticketsnew

USE revticketsnew;

-- First, let's backup any important data before modifications
-- You can manually export these if needed

-- Fix booking_seats table to ensure it has correct structure
ALTER TABLE booking_seats 
MODIFY COLUMN seat_price DECIMAL(10,2) NOT NULL;

-- Ensure bookings table has correct amount precision  
ALTER TABLE bookings
MODIFY COLUMN total_amount DECIMAL(10,2) NOT NULL;

-- Recalculate total_amount for all existing bookings based on seat prices
UPDATE bookings b
SET b.total_amount = (
    SELECT COALESCE(SUM(bs.seat_price), b.total_amount)
    FROM booking_seats bs
    WHERE bs.booking_id = b.booking_id
)
WHERE b.show_id IS NOT NULL;

-- Clean up old/stale pending bookings (older than 15 minutes)
UPDATE bookings
SET booking_status = 'FAILED', payment_status = 'FAILED'
WHERE booking_status = 'PENDING'
AND payment_status = 'PENDING'
AND booking_date < DATE_SUB(NOW(), INTERVAL 15 MINUTE);

-- Add indexes for better performance (ignore errors if they already exist)
-- CREATE INDEX idx_booking_status ON bookings(booking_status, payment_status);
-- CREATE INDEX idx_seat_availability ON seats(show_id, is_available);
-- CREATE INDEX idx_show_datetime ON shows(show_date, show_time);

-- Display summary
SELECT 
    'Total Bookings' AS metric, 
    COUNT(*) AS count 
FROM bookings
UNION ALL
SELECT 
    'Pending Bookings', 
    COUNT(*) 
FROM bookings 
WHERE booking_status = 'PENDING'
UNION ALL
SELECT 
    'Confirmed Bookings', 
    COUNT(*) 
FROM bookings 
WHERE booking_status = 'CONFIRMED'
UNION ALL
SELECT 
    'Total Seats', 
    COUNT(*) 
FROM seats
UNION ALL
SELECT 
    'Available Seats', 
    COUNT(*) 
FROM seats 
WHERE is_available = TRUE;
