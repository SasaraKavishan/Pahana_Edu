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
                       price DECIMAL(10, 2) NOT NULL.
                       category VARCHAR(100) NOT NULL DEFAULT 'Uncategorized',
                       stock INT NOT NULL DEFAULT 0
);


CREATE TABLE bills (
                       bill_id INT AUTO_INCREMENT PRIMARY KEY,
                       account_number VARCHAR(50) NOT NULL,
                       total DECIMAL(10, 2) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (account_number) REFERENCES customers(account_number)
);


CREATE TABLE bill_items (
                            bill_item_id INT AUTO_INCREMENT PRIMARY KEY,
                            bill_id INT NOT NULL,
                            item_id INT NOT NULL,
                            quantity INT NOT NULL,
                            subtotal DECIMAL(10, 2) NOT NULL,
                            FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
                            FOREIGN KEY (item_id) REFERENCES items(item_id)
);


INSERT INTO users (username, password) VALUES ('admin', 'admin123');