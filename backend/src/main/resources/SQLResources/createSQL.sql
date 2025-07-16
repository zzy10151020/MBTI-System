-- 创建数据库
CREATE DATABASE IF NOT EXISTS mbti_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mbti_test;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 用户表
CREATE TABLE `user` (
  `user_id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `role` ENUM('user', 'admin') NOT NULL DEFAULT 'user',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 问卷表
CREATE TABLE `questionnaire` (
  `questionnaire_id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `creator_id` INT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_published` BOOLEAN NOT NULL DEFAULT TRUE,
  FOREIGN KEY (`creator_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 问题表
CREATE TABLE `question` (
  `question_id` INT AUTO_INCREMENT PRIMARY KEY,
  `questionnaire_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `dimension` ENUM('E/I', 'S/N', 'T/F', 'J/P') NOT NULL,
  `question_order` SMALLINT NOT NULL,
  FOREIGN KEY (`questionnaire_id`) REFERENCES `questionnaire`(`questionnaire_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 选项表
CREATE TABLE `option` (
  `option_id` INT AUTO_INCREMENT PRIMARY KEY,
  `question_id` INT NOT NULL,
  `content` VARCHAR(255) NOT NULL,
  `score` TINYINT NOT NULL CHECK (score IN (-1, 1)),
  FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 回答表
CREATE TABLE `answer` (
  `answer_id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `questionnaire_id` INT NOT NULL,
  `answered_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`questionnaire_id`) REFERENCES `questionnaire`(`questionnaire_id`) ON DELETE CASCADE,
  INDEX `idx_user_questionnaire` (`user_id`, `questionnaire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 回答详情表
CREATE TABLE `answer_detail` (
  `detail_id` INT AUTO_INCREMENT PRIMARY KEY,
  `answer_id` INT NOT NULL,
  `question_id` INT NOT NULL,
  `option_id` INT NOT NULL,
  FOREIGN KEY (`answer_id`) REFERENCES `answer`(`answer_id`) ON DELETE CASCADE,
  FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`),
  FOREIGN KEY (`option_id`) REFERENCES `option`(`option_id`),
  INDEX `idx_answer_question` (`answer_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;