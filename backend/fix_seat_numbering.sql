-- Fix seat numbering to exclude blocked seats
USE revticketsnew;

-- First, check what we have
SELECT 
    'Before Fix' AS status,
    show_id,
    COUNT(*) AS total_seats,
    SUM(CASE WHEN is_blocked = 1 THEN 1 ELSE 0 END) AS blocked,
    SUM(CASE WHEN is_blocked = 0 THEN 1 ELSE 0 END) AS regular
FROM seats
WHERE show_id IN (14, 15)
GROUP BY show_id;

-- Delete existing seats (we'll regenerate with correct numbering)
-- But keep booked ones, mark them differently
UPDATE seats 
SET is_blocked = 1, seat_number = 0
WHERE show_id = 15 
AND is_available = 1;

-- Now regenerate all seats with proper sequential numbering per row
-- Disabled seats list
SET @disabled = 'A1,A2,A3,A4,A5,A6,A7,A8,A19,A20,A21,A22,A23,A24,A25,A26,B1,B2,B3,B4,B23,B24,B25,B26,C1,C2,C25,C26,D1,D26';
SET @base_price = 295.00;

-- Delete only the seats we just marked as blocked
DELETE FROM seats WHERE show_id = 15 AND is_blocked = 1 AND seat_number = 0;

-- Insert seats with correct numbering (sequential per row, excluding blocked)
INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available, is_blocked)
WITH RECURSIVE positions AS (
    SELECT 1 AS pos
    UNION ALL
    SELECT pos + 1 FROM positions WHERE pos < 26
),
rows AS (
    SELECT 'A' AS row_label, 1 AS row_num UNION ALL
    SELECT 'B', 2 UNION ALL SELECT 'C', 3 UNION ALL SELECT 'D', 4 UNION ALL SELECT 'E', 5 UNION ALL
    SELECT 'F', 6 UNION ALL SELECT 'G', 7 UNION ALL SELECT 'H', 8 UNION ALL SELECT 'I', 9 UNION ALL
    SELECT 'J', 10 UNION ALL SELECT 'K', 11 UNION ALL SELECT 'L', 12 UNION ALL SELECT 'M', 13 UNION ALL
    SELECT 'N', 14 UNION ALL SELECT 'O', 15 UNION ALL SELECT 'P', 16 UNION ALL SELECT 'Q', 17 UNION ALL
    SELECT 'R', 18 UNION ALL SELECT 'S', 19 UNION ALL SELECT 'T', 20 UNION ALL SELECT 'U', 21 UNION ALL
    SELECT 'V', 22 UNION ALL SELECT 'W', 23 UNION ALL SELECT 'X', 24 UNION ALL SELECT 'Y', 25 UNION ALL
    SELECT 'Z', 26
),
all_positions AS (
    SELECT 
        r.row_label,
        p.pos,
        CONCAT(r.row_label, p.pos) AS seat_label,
        FIND_IN_SET(CONCAT(r.row_label, p.pos), @disabled) > 0 AS is_blocked_seat
    FROM rows r
    CROSS JOIN positions p
),
numbered_seats AS (
    SELECT 
        row_label,
        seat_label,
        is_blocked_seat,
        CASE 
            WHEN is_blocked_seat THEN 0
            ELSE ROW_NUMBER() OVER (PARTITION BY row_label, is_blocked_seat ORDER BY pos) 
        END AS seat_number
    FROM all_positions
)
SELECT 
    15 AS show_id,
    row_label,
    seat_number,
    'REGULAR' AS seat_type,
    @base_price AS price,
    CASE WHEN is_blocked_seat THEN 0 ELSE 1 END AS is_available,
    is_blocked_seat AS is_blocked
FROM numbered_seats;

-- Mark already booked seats as unavailable (preserve their seat numbers)
UPDATE seats s
JOIN booking_seats bs ON bs.seat_id = s.seat_id
SET s.is_available = 0
WHERE s.show_id = 15;

-- Verify the fix
SELECT 
    'After Fix' AS status,
    show_id,
    COUNT(*) AS total_seats,
    SUM(CASE WHEN is_blocked = 1 THEN 1 ELSE 0 END) AS blocked,
    SUM(CASE WHEN is_blocked = 0 AND is_available = 1 THEN 1 ELSE 0 END) AS available,
    SUM(CASE WHEN is_blocked = 0 AND is_available = 0 THEN 1 ELSE 0 END) AS booked
FROM seats
WHERE show_id IN (14, 15)
GROUP BY show_id;

-- Show sample of row A to verify numbering
SELECT row_label, seat_number, is_blocked, is_available
FROM seats
WHERE show_id = 15 AND row_label = 'A'
ORDER BY seat_id
LIMIT 20;
