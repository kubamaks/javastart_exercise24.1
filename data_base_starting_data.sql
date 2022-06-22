CREATE DATABASE budget_app DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_polish_ci ;

USE budget_app;

CREATE TABLE transaction (
	id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(10) NOT NULL,
    description VARCHAR(200) NOT NULL,
    amount DECIMAL(9,2) NOT NULL,
    date DATE NOT NULL
);

 INSERT INTO transaction
(type, description, amount, date)
VALUES
('INCOME', 'salary', 5794.34, '2022-05-01'),
('OUTCOME', 'food', -235.32, '2022-05-01'),
('OUTCOME', 'rent for apartment', -650.5, '2022-05-10'),
('OUTCOME', 'bills - electricity', -230.35, '2022-05-10'),
('OUTCOME', 'bills - internet', -49.90, '2022-05-10'),
('OUTCOME', 'cimena', -28, '2022-05-11'),
('OUTCOME', 'loan instalment', -890.21, '2022-05-12'),
('OUTCOME', 'gloceries and houskeeping in biedronka (weekly shoping)', -455.50, '2022-05-12'),
('OUTCOME', 'lottery ticket', -12, '2022-05-16'),
('INCOME', 'lottery - win', 340.00, '2022-05-17'),
('OUTCOME', 'icecreams and coffe', -20, '2022-05-18');