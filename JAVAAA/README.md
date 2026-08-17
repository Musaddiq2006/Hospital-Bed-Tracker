# Hospital Bed Management System (HBMS)

A Java Swing and MySQL desktop application designed to manage, track, and optimize real-time hospital bed allocations across multiple wards.

![Java](https://img.shields.io/badge/Java-8%2B-orange?style=flat&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue?style=flat&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-green?style=flat)

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features
- **Real-Time Vacancy Tracking:** Live bed availability monitoring for ICU, General, Maternity, and Surgery wards.
- **Patient Access Portal:** Self-service check-in system with dynamic room capacity updates.
- **Admin Management Portal:** Patient audit logs, discharge management, capacity analytics, and custom ward additions.
- **CSV Data Import:** Bulk batch processing for importing external patient records into MySQL.

## Prerequisites
What tools and libraries are required before running the application?
- Java Development Kit (JDK 8+)
- MySQL Server (v8.0+)
- MySQL Connector/J (`mysql-connector-j-26.7.0.jar`)

## Installation
Step-by-step instructions to get the development environment running.

```sql
-- 1. Create and seed the MySQL database
CREATE DATABASE IF NOT EXISTS hbms_project;
USE hbms_project;

CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_type VARCHAR(50) NOT NULL UNIQUE,
    total_beds INT NOT NULL,
    vacant_beds INT NOT NULL
);

CREATE TABLE IF NOT EXISTS patient_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(100) NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO rooms (room_type, total_beds, vacant_beds) VALUES
('ICU', 10, 10),
('GENERAL WARD', 25, 25),
('MATERNITY', 20, 20),
('SURGERY', 10, 10)
ON DUPLICATE KEY UPDATE total_beds = VALUES(total_beds), vacant_beds = VALUES(vacant_beds);
JSON// 2. Link the JDBC Driver in VS Code (.vscode/settings.json)
{
    "java.project.referencedLibraries": [
        "C:/mysql-connector-j-26.7.0.jar"
    ]
}
UsageProvide clear examples of how to compile and run the project.PowerShell# Compile all Java files in the current folder
javac -cp "C:\mysql-connector-j-26.7.0.jar;." *.java

# Start the application main interface
java -cp "C:\mysql-connector-j-26.7.0.jar;." HBMSmain
ConfigurationEnvironment variables used to establish database connections.VariableTypeDescriptionDefaultDB_USERStringMySQL database usernamerootDB_PASSStringMySQL database passwordNoneDB_URLStringJDBC connection string endpointjdbc:mysql://127.0.0.1:3306/hbms_projectContributingGuidelines for how people can help improve the project.Fork the ProjectCreate your Feature Branch (git checkout -b feature/bed-analytics)Commit your Changes (git commit -m 'Add bed analytics display')Push to the Branch (git push origin feature/bed-analytics)Open a Pull RequestLicenseDistributed under the MIT License. See LICENSE for more information.ContactMusaddiq Ahmed - GitHub ProfileProject Link: https://github.com/Musaddiq2006/Hospital-Bed-Tracker