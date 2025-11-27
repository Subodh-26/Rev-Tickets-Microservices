-- Regenerate seats with proper numbering using negative numbers for blocked positions
USE revticketsnew;

-- Delete all seats for show 15 (will cascade delete booking_seats)
DELETE FROM seats WHERE show_id = 15;

SET @disabled = 'A1,A2,A3,A4,A5,A6,A7,A8,A19,A20,A21,A22,A23,A24,A25,A26,B1,B2,B3,B4,B23,B24,B25,B26,C1,C2,C25,C26,D1,D26';

-- Generate seats with proper sequential numbering
-- Blocked seats get negative seat_number based on their position
INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available, is_blocked)
WITH RECURSIVE 
rows AS (
    SELECT 'A' AS row_label, 0 AS row_idx UNION ALL
    SELECT 'B', 1 UNION ALL SELECT 'C', 2 UNION ALL SELECT 'D', 3 UNION ALL SELECT 'E', 4 UNION ALL
    SELECT 'F', 5 UNION ALL SELECT 'G', 6 UNION ALL SELECT 'H', 7 UNION ALL SELECT 'I', 8 UNION ALL SELECT 'J', 9 UNION ALL
    SELECT 'K', 10 UNION ALL SELECT 'L', 11 UNION ALL SELECT 'M', 12 UNION ALL SELECT 'N', 13 UNION ALL SELECT 'O', 14 UNION ALL
    SELECT 'P', 15 UNION ALL SELECT 'Q', 16 UNION ALL SELECT 'R', 17 UNION ALL SELECT 'S', 18 UNION ALL SELECT 'T', 19 UNION ALL
    SELECT 'U', 20 UNION ALL SELECT 'V', 21 UNION ALL SELECT 'W', 22 UNION ALL SELECT 'X', 23 UNION ALL SELECT 'Y', 24 UNION ALL SELECT 'Z', 25
),
positions AS (
    SELECT 1 AS pos UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26
),
all_seats AS (
    SELECT 
        r.row_label,
        r.row_idx,
        p.pos,
        CONCAT(r.row_label, p.pos) AS seat_label,
        FIND_IN_SET(CONCAT(r.row_label, p.pos), @disabled) > 0 AS is_blocked
    FROM rows r
    CROSS JOIN positions p
),
numbered_seats AS (
    SELECT 
        row_label,
        pos,
        is_blocked,
        CASE 
            WHEN is_blocked THEN -pos  -- Negative position for blocked seats
            ELSE (
                SELECT COUNT(*) 
                FROM all_seats AS s2
                WHERE s2.row_label = all_seats.row_label
                AND s2.pos <= all_seats.pos
                AND s2.is_blocked = 0
            )
        END AS seat_number
    FROM all_seats
)
SELECT 
    15 AS show_id,
    row_label,
    seat_number,
    'REGULAR' AS seat_type,
    295.00 AS price,
    CASE WHEN is_blocked THEN 0 ELSE 1 END AS is_available,
    is_blocked
FROM numbered_seats
ORDER BY row_label, pos;

-- Verify Row A
SELECT 'Row A' AS info, row_label, seat_number, is_blocked, is_available
FROM seats
WHERE show_id = 15 AND row_label = 'A'
ORDER BY seat_id;

-- Summary
SELECT 
    'Summary' AS info,
    COUNT(*) AS total_seats,
    SUM(CASE WHEN is_blocked = 1 THEN 1 ELSE 0 END) AS blocked,
    SUM(CASE WHEN is_blocked = 0 THEN 1 ELSE 0 END) AS bookable,
    MAX(CASE WHEN is_blocked = 0 THEN seat_number ELSE 0 END) AS max_seat_num
FROM seats
WHERE show_id = 15;
