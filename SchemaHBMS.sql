-- HBMS Database Schema
CREATE DATABASE IF NOT EXISTS hbms_project;
USE hbms_project;

-- Table for Room Data
CREATE TABLE IF NOT EXISTS rooms (
    room_type VARCHAR(50) PRIMARY KEY,
    total_beds INT,
    vacant_beds INT
);

-- Table for Patient Records
CREATE TABLE IF NOT EXISTS patient_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(100),
    room_type VARCHAR(50),
    action_type VARCHAR(20),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default Data to get started
INSERT INTO rooms (room_type, total_beds, vacant_beds) VALUES 
('ICU', 10, 10), 
('GENERAL WARD', 50, 50), 
('MATERNITY', 20, 20)
ON DUPLICATE KEY UPDATE total_beds=total_beds;