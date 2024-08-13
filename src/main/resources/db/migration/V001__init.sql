CREATE TABLE booking_status_enum
(
    name VARCHAR(255) PRIMARY KEY
);

INSERT INTO booking_status_enum (name)
VALUES ('PENDING'),
       ('CONFIRMED'),
       ('COMPLETED'),
       ('CANCELLED');

CREATE TABLE taxi_status_enum
(
    name VARCHAR(255) PRIMARY KEY
);

INSERT INTO taxi_status_enum (name)
VALUES ('AVAILABLE'),
       ('BOOKED'),
       ('UNAVAILABLE');

CREATE TABLE passengers
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    created_at   TIMESTAMP    NOT NULL
);

CREATE TABLE locations
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    latitude  DOUBLE,
    longitude DOUBLE
);

CREATE TABLE taxis
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(255) NOT NULL,
    location_id   BIGINT,
    status        VARCHAR(255) CHECK (status IN ('AVAILABLE', 'BOOKED', 'UNAVAILABLE')),
    is_deleted    BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (location_id) REFERENCES locations (id),
    FOREIGN KEY (status) REFERENCES taxi_status_enum (name)
);

CREATE TABLE bookings
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    passenger_id        BIGINT       NOT NULL,
    pickup_location_id  BIGINT       NOT NULL,
    dropoff_location_id BIGINT       NOT NULL,
    status              VARCHAR(255) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED')),
    booked_at           TIMESTAMP    NOT NULL,
    taxi_id             BIGINT,
    FOREIGN KEY (passenger_id) REFERENCES passengers (id),
    FOREIGN KEY (pickup_location_id) REFERENCES locations (id),
    FOREIGN KEY (dropoff_location_id) REFERENCES locations (id),
    FOREIGN KEY (taxi_id) REFERENCES taxis (id),
    FOREIGN KEY (status) REFERENCES booking_status_enum (name)
);

CREATE INDEX idx_bookings_status ON bookings(status);

CREATE INDEX idx_bookings_booked_at ON bookings(booked_at);

CREATE INDEX idx_bookings_pickup_dropoff ON bookings(pickup_location_id, dropoff_location_id);
