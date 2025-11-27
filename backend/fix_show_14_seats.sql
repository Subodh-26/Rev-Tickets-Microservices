-- Fix seats for show 14
USE revticketsnew;

-- Delete existing seats
DELETE FROM seats WHERE show_id = 14;

-- Get the base price for the show
SET @base_price = 295.00;

-- Disabled seats list
SET @disabled = 'A1,A2,A3,A4,A5,A6,A7,A8,A19,A20,A21,A22,A23,A24,A25,A26,B1,B2,B3,B4,B23,B24,B25,B26,C1,C2,C25,C26,D1,D26';

-- Generate seats for all 26 rows (A-Z), 26 seats each
INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available)
SELECT 
    14 AS show_id,
    row_label,
    seat_number,
    CASE 
        WHEN ROW_NUMBER() OVER (PARTITION BY row_label ORDER BY seat_number) <= 2 
             AND row_label IN ('A', 'B') THEN 'PREMIUM'
        WHEN row_label IN ('A', 'B', 'C', 'D', 'E') THEN 'REGULAR'
        ELSE 'ECONOMY'
    END AS seat_type,
    CASE 
        WHEN row_label IN ('A', 'B') THEN @base_price * 1.5
        WHEN row_label IN ('C', 'D', 'E') THEN @base_price * 1.2
        ELSE @base_price * 1.0
    END AS price,
    CASE 
        WHEN FIND_IN_SET(CONCAT(row_label, seat_number), @disabled) > 0 THEN 0
        ELSE 1
    END AS is_available
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
) AS seat_data;

-- Verify the results
SELECT 
    'Total seats' AS metric,
    COUNT(*) AS count
FROM seats
WHERE show_id = 14
UNION ALL
SELECT 
    'Available seats',
    COUNT(*)
FROM seats
WHERE show_id = 14 AND is_available = 1
UNION ALL
SELECT 
    'Disabled seats',
    COUNT(*)
FROM seats
WHERE show_id = 14 AND is_available = 0;
