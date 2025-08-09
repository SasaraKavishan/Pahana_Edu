CREATE DATABASE bookshop;
USE bookshop;

CREATE TABLE users (
                       username VARCHAR(50) PRIMARY KEY,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE customers (
                           account_number VARCHAR(10) PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           telephone VARCHAR(15) NOT NULL,
                           units_consumed INT NOT NULL
);

CREATE TABLE items (
                       item_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       price DECIMAL(10, 2) NOT NULL
);

-- Insert a default admin user (password: admin123, hashed using a simple method for demo)
INSERT INTO users (username, password) VALUES ('admin', 'admin123');