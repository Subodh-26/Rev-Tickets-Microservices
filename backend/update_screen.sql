USE revticketsnew;

UPDATE screens SET 
  seat_layout = '{"rows": ["A", "B", "C", "D", "E", "F", "G", "H"], "totalSeats": 80, "seatsPerRow": {"A": 20, "B": 20, "C": 20, "D": 20, "E": 20, "F": 20, "G": 20, "H": 20}, "disabledSeats": ["A1", "A2", "A3", "A4", "A5", "A17", "A18", "A19", "A20", "B1", "B2", "B3", "B4", "B5", "B17", "B18", "B19", "B20", "C1", "C2", "C3", "C15", "C16", "C17", "C18", "C19", "C20", "D1", "D2", "D3", "D15", "D16", "D17", "D18", "D19", "D20", "E1", "E2", "E10", "E11", "E12", "E13", "E14", "E15", "E16", "E17", "E18", "E19", "E20", "F1", "F2", "F10", "F11", "F12", "F13", "F14", "F15", "F16", "F17", "F18", "F19", "F20", "G1", "G2", "G10", "G11", "G12", "G13", "G14", "G15", "G16", "G17", "G18", "G19", "G20", "H1", "H2", "H10", "H11", "H12", "H13", "H14", "H15", "H16", "H17", "H18", "H19", "H20"]}',
  total_seats = 80
WHERE screen_id = 2;

SELECT 'Screen updated successfully' as status;
