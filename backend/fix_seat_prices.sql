-- Fix seat prices - all seats should have the show's base price (no tier pricing)
USE revticketsnew;

-- Update all seats to have their show's base price
UPDATE seats s
JOIN shows sh ON s.show_id = sh.show_id
SET s.price = sh.base_price,
    s.seat_type = 'REGULAR'
WHERE s.show_id IN (14, 15);

-- Verify the fix
SELECT 
    'Show 14' AS show_name,
    COUNT(*) AS total_seats,
    COUNT(DISTINCT price) AS unique_prices,
    MIN(price) AS min_price,
    MAX(price) AS max_price,
    AVG(price) AS avg_price
FROM seats
WHERE show_id = 14
UNION ALL
SELECT 
    'Show 15',
    COUNT(*),
    COUNT(DISTINCT price),
    MIN(price),
    MAX(price),
    AVG(price)
FROM seats
WHERE show_id = 15;

-- Also update any existing bookings' seat prices
UPDATE booking_seats bs
JOIN seats s ON bs.seat_id = s.seat_id
SET bs.seat_price = s.price
WHERE s.show_id IN (14, 15);

-- Recalculate booking totals
UPDATE bookings b
SET b.total_amount = (
    SELECT COALESCE(SUM(bs.seat_price), 0)
    FROM booking_seats bs
    WHERE bs.booking_id = b.booking_id
)
WHERE b.show_id IN (14, 15);

-- Show summary
SELECT 
    b.booking_id,
    b.booking_reference,
    b.show_id,
    b.total_seats,
    b.total_amount,
    COUNT(bs.booking_seat_id) AS seat_count,
    SUM(bs.seat_price) AS calculated_total
FROM bookings b
LEFT JOIN booking_seats bs ON b.booking_id = bs.booking_id
WHERE b.show_id IN (14, 15)
GROUP BY b.booking_id
ORDER BY b.booking_id DESC;
