CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'user') DEFAULT 'user',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Insert sample users (password is 'password123' hashed with BCrypt)
INSERT INTO users (username, password, full_name, role) VALUES
('admin', '$2a$10$CSMqDoVNX8cCjsMGS42h.O05hxJklCzONz3cbtHUTo7yPJVNLHlJC', 'Admin User', 'admin'),
('john', '$2a$10$CSMqDoVNX8cCjsMGS42h.O05hxJklCzONz3cbtHUTo7yPJVNLHlJC', 'John Doe', 'user'),
('jane', '$2a$10$CSMqDoVNX8cCjsMGS42h.O05hxJklCzONz3cbtHUTo7yPJVNLHlJC', 'Jane Smith', 'user');