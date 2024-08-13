INSERT INTO passengers (id, name, phone_number, created_at)
VALUES (1, 'John Doe', '123-456-7890', '2023-08-01 10:00:00'),
       (2, 'Jane Smith', '234-567-8901', '2023-08-01 11:00:00'),
       (3, 'Alice Johnson', '345-678-9012', '2023-08-01 12:00:00');

INSERT INTO locations (id, latitude, longitude)
VALUES (1, 25.276987, 55.296249), -- Dubai
       (2, 24.453884, 54.377344), -- Abu Dhabi
       (3, 25.204849, 55.270782); -- Sharjah

INSERT INTO taxis (id, license_plate, location_id, status, is_deleted)
VALUES (1, 'A12345', 1, 'AVAILABLE', FALSE),
       (2, 'B23456', 2, 'BOOKED', FALSE),
       (3, 'C34567', 3, 'UNAVAILABLE', FALSE);

INSERT INTO bookings (id, passenger_id, pickup_location_id, dropoff_location_id, status, booked_at, taxi_id)
VALUES (1, 1, 1, 2, 'CONFIRMED', '2023-08-01 13:00:00', 1),
       (2, 2, 2, 3, 'PENDING', '2023-08-01 14:00:00', 2),
       (3, 3, 3, 1, 'COMPLETED', '2023-08-01 15:00:00', 3);
