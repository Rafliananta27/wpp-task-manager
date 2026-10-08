-- Run once as a local MariaDB/MySQL administrator. Development credentials only.
CREATE DATABASE IF NOT EXISTS task_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'task_user'@'localhost' IDENTIFIED BY 'local_task_password';
GRANT ALL PRIVILEGES ON task_manager.* TO 'task_user'@'localhost';
