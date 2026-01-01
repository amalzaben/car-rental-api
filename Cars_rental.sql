-- Drop and recreate (for dev only)
DROP DATABASE IF EXISTS cars_rental;
CREATE DATABASE cars_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cars_rental;

-- =========================
-- cars
-- =========================
CREATE TABLE cars (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  family VARCHAR(20) NOT NULL,
  type VARCHAR(20) NOT NULL,
  seats INT NOT NULL,
  fuel VARCHAR(20),
  price DECIMAL(10,2),
  description TEXT,
  picture VARCHAR(255),
  status ENUM('available', 'booked', 'in_maintenance') NOT NULL DEFAULT 'available'
);

-- =========================
-- colors
-- =========================
CREATE TABLE colors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(25) NOT NULL UNIQUE
);

-- =========================
-- car_color 
-- =========================
CREATE TABLE car_color (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  car_id BIGINT NOT NULL,
  color_id BIGINT NOT NULL,
  CONSTRAINT fk_car_color_car
    FOREIGN KEY (car_id) REFERENCES cars(id),
  CONSTRAINT fk_car_color_color
    FOREIGN KEY (color_id) REFERENCES colors(id),
  CONSTRAINT uq_car_color UNIQUE (car_id, color_id)
);

-- =========================
-- users
-- =========================
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(25) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  profile_picture VARCHAR(255),
  role ENUM('customer', 'manager', 'employee') NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================
-- employees (unique id + user_id)
-- =========================
CREATE TABLE employees (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  name VARCHAR(30),
  email VARCHAR(60),
  phone_number VARCHAR(20),
  dob DATE,
  role ENUM('delivery', 'customer_service', 'online_booking_officer'),
  CONSTRAINT fk_employees_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================
-- payments (added because bookings references payment_id)
-- =========================
CREATE TABLE payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  amount DECIMAL(10,2) NOT NULL,
  method ENUM('cash', 'card', 'wallet', 'other') NOT NULL DEFAULT 'other',
  status ENUM('pending', 'paid', 'failed', 'refunded') NOT NULL DEFAULT 'pending',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- bookings
-- =========================
CREATE TABLE bookings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  car_id BIGINT NOT NULL,
  car_color_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  location VARCHAR(60) NOT NULL,
  phone_number VARCHAR(25) NOT NULL,
  personal_id VARCHAR(30),
  total_price DECIMAL(10,2),
  payment_id BIGINT NOT NULL,
  status ENUM('pending', 'approved', 'rejected', 'cancelled') NOT NULL DEFAULT 'pending',
  handled_by_employee_id BIGINT,
  added_manually_by_employee_id BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_bookings_customer
    FOREIGN KEY (customer_id) REFERENCES users(id),

  CONSTRAINT fk_bookings_car
    FOREIGN KEY (car_id) REFERENCES cars(id),

  CONSTRAINT fk_bookings_car_color
    FOREIGN KEY (car_color_id) REFERENCES car_color(id),

  CONSTRAINT fk_bookings_payment
    FOREIGN KEY (payment_id) REFERENCES payments(id),

  CONSTRAINT fk_bookings_handled_by
    FOREIGN KEY (handled_by_employee_id) REFERENCES employees(id),

  CONSTRAINT fk_bookings_added_manually_by
    FOREIGN KEY (added_manually_by_employee_id) REFERENCES employees(id)
);

-- =========================
-- deliveries
-- =========================
CREATE TABLE deliveries (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_id BIGINT,
  type ENUM('pickup', 'dropoff'),
  status ENUM('available', 'assigned', 'completed') NOT NULL DEFAULT 'available',
  assigned_employee_id BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME,

  CONSTRAINT fk_deliveries_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(id),

  CONSTRAINT fk_deliveries_assigned_employee
    FOREIGN KEY (assigned_employee_id) REFERENCES employees(id)
);

-- =========================
-- favorite_cars (no composite key)
-- =========================
CREATE TABLE favorite_cars (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  car_id BIGINT NOT NULL,

  CONSTRAINT fk_favorite_cars_user
    FOREIGN KEY (user_id) REFERENCES users(id),

  CONSTRAINT fk_favorite_cars_car
    FOREIGN KEY (car_id) REFERENCES cars(id),

  CONSTRAINT uq_favorite_cars UNIQUE (user_id, car_id)
);

-- =========================
-- customer_notifications
-- =========================
CREATE TABLE customer_notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_id BIGINT,
  message TEXT,
  type ENUM('booking_confirmed', 'booking_rejected', 'return_reminder') NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_customer_notifications_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- =========================
-- manager_notifications
-- =========================
CREATE TABLE manager_notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type ENUM('booking_created', 'booking_cancelled', 'car_maintenance') NOT NULL,
  booking_id BIGINT,
  message TEXT,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_manager_notifications_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);
