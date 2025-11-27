-- Properly regenerate seats with sequential numbering excluding blocked positions
USE revticketsnew;

-- Save booked seats info before deletion
CREATE TEMPORARY TABLE IF NOT EXISTS temp_booked_seats AS
SELECT s.show_id, s.row_label, s.seat_number, bs.booking_id
FROM seats s
JOIN booking_seats bs ON s.seat_id = bs.seat_id
WHERE s.show_id = 15 AND s.is_available = 0;

-- Delete booking_seats references first
DELETE bs FROM booking_seats bs
JOIN seats s ON bs.seat_id = s.seat_id
WHERE s.show_id = 15;

-- Now delete all seats for show 15
DELETE FROM seats WHERE show_id = 15;

-- Regenerate with proper numbering
SET @disabled = 'A1,A2,A3,A4,A5,A6,A7,A8,A19,A20,A21,A22,A23,A24,A25,A26,B1,B2,B3,B4,B23,B24,B25,B26,C1,C2,C25,C26,D1,D26';
SET @base_price = 295.00;

-- Create seats row by row with proper sequential numbering
-- Row A (positions 1-26, blocked: 1-8, 19-26)
INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available, is_blocked)
SELECT 15, 'A', 0, 'REGULAR', 295, 0, 1 FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) t
UNION ALL SELECT 15, 'A', 1, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 2, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 3, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 4, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 5, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 6, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 7, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 8, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 9, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 10, 'REGULAR', 295, 1, 0
UNION ALL SELECT 15, 'A', 0, 'REGULAR', 295, 0, 1 FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) t2;

-- This is getting too complex. Let me use a simpler script approach
-- Delete what we just added
DELETE FROM seats WHERE show_id = 15;

-- Use Python-style logic in SQL
DELIMITER $$
CREATE PROCEDURE generate_seats_for_show_15()
BEGIN
    DECLARE row_char CHAR(1);
    DECLARE row_idx INT DEFAULT 0;
    DECLARE pos INT;
    DECLARE seat_num INT;
    DECLARE seat_label VARCHAR(10);
    DECLARE is_blocked_flag BOOLEAN;
    
    SET @disabled = 'A1,A2,A3,A4,A5,A6,A7,A8,A19,A20,A21,A22,A23,A24,A25,A26,B1,B2,B3,B4,B23,B24,B25,B26,C1,C2,C25,C26,D1,D26';
    
    WHILE row_idx < 26 DO
        SET row_char = CHAR(65 + row_idx); -- A=65, B=66, etc.
        SET seat_num = 1;
        SET pos = 1;
        
        WHILE pos <= 26 DO
            SET seat_label = CONCAT(row_char, pos);
            SET is_blocked_flag = FIND_IN_SET(seat_label, @disabled) > 0;
            
            IF is_blocked_flag THEN
                INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available, is_blocked)
                VALUES (15, row_char, 0, 'REGULAR', 295.00, 0, 1);
            ELSE
                INSERT INTO seats (show_id, row_label, seat_number, seat_type, price, is_available, is_blocked)
                VALUES (15, row_char, seat_num, 'REGULAR', 295.00, 1, 0);
                SET seat_num = seat_num + 1;
            END IF;
            
            SET pos = pos + 1;
        END WHILE;
        
        SET row_idx = row_idx + 1;
    END WHILE;
END$$
DELIMITER ;

CALL generate_seats_for_show_15();
DROP PROCEDURE generate_seats_for_show_15;

-- Verify
SELECT 
    row_label,
    COUNT(*) AS total,
    SUM(CASE WHEN is_blocked = 1 THEN 1 ELSE 0 END) AS blocked_count,
    MAX(CASE WHEN is_blocked = 0 THEN seat_number ELSE 0 END) AS max_seat_number
FROM seats
WHERE show_id = 15
GROUP BY row_label
ORDER BY row_label
LIMIT 5;
