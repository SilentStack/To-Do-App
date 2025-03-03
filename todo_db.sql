CREATE database if NOT EXISTS todo_db;

USE todo_db;

CREATE TABLE tasks(
    id INT AUTO_INCREMENT PRIMARY KEY, 
    description VARCHAR (255) NOT NULL,
    isCompleted BOOLEAN DEFAULT FALSE
);
SELECT * FROM tasks