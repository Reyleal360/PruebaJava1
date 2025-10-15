-- ============================================
-- LibroNova Database Schema - SIMPLIFIED VERSION
-- MySQL Database Setup
-- Author: LibroNova Team
-- Version: 1.0
-- ============================================

-- Drop database if exists
DROP DATABASE IF EXISTS libronova;

-- Create database
CREATE DATABASE libronova 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE libronova;

-- ============================================
-- TABLE: users
-- System users (librarians, administrators)
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- TABLE: books
-- Book catalog
-- ============================================
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(200) NOT NULL,
    publisher VARCHAR(200),
    publication_year INT,
    category VARCHAR(100) NOT NULL,
    available_stock INT NOT NULL DEFAULT 0,
    total_stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- TABLE: members
-- Library members
-- ============================================
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_id VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    registration_date DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    membership_type VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- TABLE: loans
-- Book loan transactions
-- ============================================
CREATE TABLE loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    expected_return_date DATE NOT NULL,
    actual_return_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    penalty DECIMAL(10, 2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ============================================
-- INSERT TEST DATA
-- ============================================

-- Insert Users
INSERT INTO users (username, password, first_name, last_name, email, role, active) VALUES
('admin', 'admin123', 'System', 'Administrator', 'admin@libronova.com', 'ADMINISTRATOR', TRUE),
('librarian1', 'lib123', 'María', 'González', 'maria.gonzalez@libronova.com', 'LIBRARIAN', TRUE),
('librarian2', 'lib123', 'Carlos', 'Rodríguez', 'carlos.rodriguez@libronova.com', 'LIBRARIAN', TRUE),
('assistant1', 'asst123', 'Ana', 'Martínez', 'ana.martinez@libronova.com', 'ASSISTANT', TRUE);

-- Insert Books
INSERT INTO books (isbn, title, author, publisher, publication_year, category, available_stock, total_stock) VALUES
-- Fiction
('978-0-06-112008-4', 'To Kill a Mockingbird', 'Harper Lee', 'J.B. Lippincott', 1960, 'Fiction', 3, 5),
('978-0-14-028329-5', '1984', 'George Orwell', 'Secker & Warburg', 1949, 'Fiction', 2, 4),
('978-0-7432-7356-5', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Scribner', 1925, 'Fiction', 4, 6),
('978-0-452-28423-4', 'The Catcher in the Rye', 'J.D. Salinger', 'Little, Brown', 1951, 'Fiction', 2, 3),
('978-0-316-76948-0', 'Pride and Prejudice', 'Jane Austen', 'T. Egerton', 1813, 'Fiction', 5, 7),

-- Science
('978-0-553-38016-3', 'A Brief History of Time', 'Stephen Hawking', 'Bantam Books', 1988, 'Science', 3, 4),
('978-0-385-47381-9', 'Cosmos', 'Carl Sagan', 'Random House', 1980, 'Science', 2, 3),
('978-0-393-31995-3', 'The Selfish Gene', 'Richard Dawkins', 'Oxford University', 1976, 'Science', 2, 3),

-- Technology
('978-0-13-110362-7', 'The C Programming Language', 'Brian Kernighan', 'Prentice Hall', 1978, 'Technology', 4, 5),
('978-0-201-63361-0', 'Design Patterns', 'Gang of Four', 'Addison-Wesley', 1994, 'Technology', 3, 4),
('978-0-13-468599-1', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 2008, 'Technology', 5, 6),
('978-0-134-68599-4', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 2018, 'Technology', 2, 3),

-- History
('978-0-375-41398-8', 'Guns, Germs, and Steel', 'Jared Diamond', 'W.W. Norton', 1997, 'History', 2, 3),
('978-0-679-64115-3', 'Sapiens', 'Yuval Noah Harari', 'Harvill Secker', 2014, 'History', 4, 6),

-- Biography
('978-0-7432-7357-2', 'Steve Jobs', 'Walter Isaacson', 'Simon & Schuster', 2011, 'Biography', 3, 4),
('978-1-5011-2739-9', 'Leonardo da Vinci', 'Walter Isaacson', 'Simon & Schuster', 2017, 'Biography', 2, 3),

-- Children
('978-0-06-440055-8', 'Charlotte''s Web', 'E.B. White', 'Harper & Brothers', 1952, 'Children', 5, 7),
('978-0-590-35340-3', 'Harry Potter Stone', 'J.K. Rowling', 'Bloomsbury', 1997, 'Children', 0, 8),
('978-0-394-80001-1', 'The Cat in the Hat', 'Dr. Seuss', 'Random House', 1957, 'Children', 4, 6),

-- Non-Fiction
('978-0-7432-7356-6', 'Thinking Fast and Slow', 'Daniel Kahneman', 'Farrar Straus', 2011, 'Non-Fiction', 3, 5),
('978-1-4516-7389-7', 'The Power of Habit', 'Charles Duhigg', 'Random House', 2012, 'Non-Fiction', 4, 5);

-- Insert Members
INSERT INTO members (member_number, first_name, last_name, document_id, email, phone, address, registration_date, active, membership_type) VALUES
('MEM-20250101-A001', 'Juan', 'Pérez', '1234567890', 'juan.perez@email.com', '555-0101', 'Calle 45 #23-45', '2024-01-15', TRUE, 'BASIC'),
('MEM-20250102-A002', 'Laura', 'Martínez', '1234567891', 'laura.martinez@email.com', '555-0102', 'Carrera 30 #50-20', '2024-02-20', TRUE, 'PREMIUM'),
('MEM-20250103-A003', 'Pedro', 'García', '1234567892', 'pedro.garcia@email.com', '555-0103', 'Avenida 40 #15-30', '2024-03-10', TRUE, 'VIP'),
('MEM-20250104-A004', 'Sofia', 'López', '1234567893', 'sofia.lopez@email.com', '555-0104', 'Calle 70 #25-15', '2024-04-05', TRUE, 'BASIC'),
('MEM-20250105-A005', 'Miguel', 'Ramírez', '1234567894', 'miguel.ramirez@email.com', '555-0105', 'Carrera 15 #80-40', '2024-05-12', TRUE, 'PREMIUM'),
('MEM-20250106-A006', 'Carmen', 'Torres', '1234567895', 'carmen.torres@email.com', '555-0106', 'Calle 90 #35-25', '2024-06-18', TRUE, 'BASIC'),
('MEM-20250107-A007', 'Diego', 'Fernández', '1234567896', 'diego.fernandez@email.com', '555-0107', 'Avenida 60 #45-50', '2024-07-22', TRUE, 'VIP'),
('MEM-20250108-A008', 'Isabella', 'Morales', '1234567897', 'isabella.morales@email.com', '555-0108', 'Calle 100 #20-10', '2024-08-30', TRUE, 'PREMIUM'),
('MEM-20250109-A009', 'Andrés', 'Castro', '1234567898', 'andres.castro@email.com', '555-0109', 'Carrera 25 #55-35', '2024-09-14', FALSE, 'BASIC'),
('MEM-20250110-A010', 'Valentina', 'Jiménez', '1234567899', 'valentina.jimenez@email.com', '555-0110', 'Calle 80 #40-20', '2024-10-01', TRUE, 'BASIC');

-- Insert Loans (some active, some returned)
INSERT INTO loans (member_id, book_id, user_id, loan_date, expected_return_date, actual_return_date, status, penalty, notes) VALUES
-- Active loans
(1, 1, 2, '2024-10-01', '2024-10-16', NULL, 'ACTIVE', 0.00, 'First loan'),
(2, 3, 2, '2024-10-05', '2024-10-20', NULL, 'ACTIVE', 0.00, NULL),
(3, 10, 2, '2024-10-08', '2024-10-23', NULL, 'ACTIVE', 0.00, 'Technology book'),
(5, 15, 1, '2024-10-10', '2024-10-25', NULL, 'ACTIVE', 0.00, NULL),

-- Overdue loans (past expected return date)
(4, 5, 2, '2024-09-20', '2024-10-05', NULL, 'ACTIVE', 0.00, 'Should calculate penalty'),
(6, 7, 1, '2024-09-25', '2024-10-10', NULL, 'ACTIVE', 0.00, 'Overdue'),

-- Returned loans
(1, 2, 2, '2024-09-01', '2024-09-16', '2024-09-15', 'RETURNED', 0.00, 'Returned on time'),
(2, 4, 1, '2024-09-05', '2024-09-20', '2024-09-18', 'RETURNED', 0.00, 'Returned early'),
(3, 6, 2, '2024-08-15', '2024-08-30', '2024-09-05', 'OVERDUE', 9.00, 'Returned late, 6 days overdue'),
(5, 8, 1, '2024-08-20', '2024-09-04', '2024-09-03', 'RETURNED', 0.00, 'Returned on time'),
(7, 11, 2, '2024-07-10', '2024-07-25', '2024-07-24', 'RETURNED', 0.00, 'Good return'),
(8, 14, 1, '2024-07-15', '2024-07-30', '2024-08-02', 'OVERDUE', 4.50, '3 days late');

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Verify data insertion
SELECT 'Users count:' as Info, COUNT(*) as Total FROM users
UNION ALL
SELECT 'Books count:', COUNT(*) FROM books
UNION ALL
SELECT 'Members count:', COUNT(*) FROM members
UNION ALL
SELECT 'Loans count:', COUNT(*) FROM loans;

-- Show summary
SELECT 
    'Database Created Successfully!' as Status,
    'libronova' as Database_Name,
    NOW() as Created_At;
