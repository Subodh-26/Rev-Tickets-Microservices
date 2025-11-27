-- Regenerate seats for show 15 - exclude disabled seats completely
USE revticketsnew;

-- Delete all existing seats for show 15
DELETE FROM seats WHERE show_id = 15;

-- Disabled seats that should NOT be created
SET @disabled = 'A1,A2,A3,A4,A5,A6,A7,A8,A19,A20,A21,A22,A23,A24,A25,A26,B1,B2,B3,B4,B23,B24,B25,B26,C1,C2,C25,C26,D1,D26';

-- Get base price
SET @base_price = 295.00;

-- Generate seats (26 rows × 26 seats each, minus 30 disabled = 646 seats)
INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available)
SELECT 
    15 AS show_id,
    row_label,
    seat_number,
    'REGULAR' AS seat_type,
    @base_price AS price,
    1 AS is_available
FROM (
    SELECT 'A' AS row_label UNION ALL SELECT 'B' UNION ALL SELECT 'C' UNION ALL SELECT 'D' UNION ALL SELECT 'E' UNION ALL
    SELECT 'F' UNION ALL SELECT 'G' UNION ALL SELECT 'H' UNION ALL SELECT 'I' UNION ALL SELECT 'J' UNION ALL
    SELECT 'K' UNION ALL SELECT 'L' UNION ALL SELECT 'M' UNION ALL SELECT 'N' UNION ALL SELECT 'O' UNION ALL
    SELECT 'P' UNION ALL SELECT 'Q' UNION ALL SELECT 'R' UNION ALL SELECT 'S' UNION ALL SELECT 'T' UNION ALL
    SELECT 'U' UNION ALL SELECT 'V' UNION ALL SELECT 'W' UNION ALL SELECT 'X' UNION ALL SELECT 'Y' UNION ALL SELECT 'Z'
) AS row_data
CROSS JOIN (
    SELECT 1 AS seat_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26
) AS seat_data
WHERE NOT FIND_IN_SET(CONCAT(row_label, seat_number), @disabled);

-- Mark already booked seats as unavailable
UPDATE seats s
JOIN booking_seats bs ON s.show_id = 15 AND s.row_label = SUBSTRING_INDEX(bs.seat_label, REGEXP_REPLACE(bs.seat_label, '[0-9]', ''), -1)
SET s.is_available = 0
WHERE s.show_id = 15;

-- Simpler approach - mark seats from booking 23 and 24 as unavailable
UPDATE seats s
SET s.is_available = 0
WHERE s.show_id = 15
  AND CONCAT(s.row_label, s.seat_number) IN ('C10', 'C11', 'C12', 'E20');

-- Verify
SELECT 
    'Total seats created' AS metric,
    COUNT(*) AS count
FROM seats
WHERE show_id = 15
UNION ALL
SELECT 
    'Available seats',
    COUNT(*)
FROM seats
WHERE show_id = 15 AND is_available = 1
UNION ALL
SELECT 
    'Booked seats',
    COUNT(*)
FROM seats
WHERE show_id = 15 AND is_available = 0
UNION ALL
SELECT
    'Unique prices',
    COUNT(DISTINCT price)
FROM seats
WHERE show_id = 15;
