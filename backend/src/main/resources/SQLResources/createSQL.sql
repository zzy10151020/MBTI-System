-- 创建数据库
CREATE DATABASE IF NOT EXISTS mbti_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mbti_system;

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

INSERT INTO `user` (`username`, `password_hash`, `email`, `role`)
VALUES 
  ('admin', SHA2('admin123', 256), 'admin@example.com', 'admin'),
  ('alice', SHA2('alice123', 256), 'alice@example.com', 'user'),
  ('bob', SHA2('bob123', 256), 'bob@example.com', 'user'),
  ('carol', SHA2('carol123', 256), 'carol@example.com', 'user');

INSERT INTO `questionnaire` (`title`, `description`, `creator_id`)
VALUES 
  ('MBTI 性格测试 v1', '测试你的 MBTI 性格类型（标准版）', 1),
  ('MBTI 快速测试', '快速判断你的 MBTI 类型', 1),
  ('16型人格深入测评', '更深入的 MBTI 测评问卷', 1);

-- 问卷 1
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`)
VALUES 
  (1, '你喜欢独处还是与人交往？', 'E/I', 1),
  (1, '你更倾向依赖直觉还是实际？', 'S/N', 2),
  (1, '你做决定看重逻辑还是情感？', 'T/F', 3),
  (1, '你更喜欢计划还是随机应变？', 'J/P', 4);

-- 问卷 2
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`)
VALUES 
  (2, '你在派对上会主动找人聊天吗？', 'E/I', 1),
  (2, '你更相信经验还是直觉？', 'S/N', 2),
  (2, '你更容易被逻辑说服还是感情打动？', 'T/F', 3),
  (2, '你是否总喜欢提前安排好一切？', 'J/P', 4);

-- 问卷 3
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`)
VALUES 
  (3, '遇到困难时你倾向独自处理还是找人帮忙？', 'E/I', 1),
  (3, '你更关注细节还是大局？', 'S/N', 2),
  (3, '你更常基于事实还是感觉做判断？', 'T/F', 3),
  (3, '你喜欢做列表还是随心所欲？', 'J/P', 4);

-- E/I 题
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(1, '我喜欢社交', 1), (1, '我更喜欢独处', -1),
(5, '会主动聊天', 1), (5, '比较安静', -1),
(9, '自己处理', -1), (9, '会找人帮忙', 1);

-- S/N
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(2, '依赖直觉', 1), (2, '依赖现实', -1),
(6, '相信直觉', 1), (6, '看重经验', -1),
(10, '关注大局', 1), (10, '关注细节', -1);

-- T/F
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(3, '看重逻辑', 1), (3, '看重情感', -1),
(7, '逻辑说服我', 1), (7, '感情打动我', -1),
(11, '基于事实', 1), (11, '凭感觉', -1);

-- J/P
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(4, '喜欢计划', 1), (4, '喜欢随意', -1),
(8, '提前安排', 1), (8, '灵活变通', -1),
(12, '做列表', 1), (12, '随心所欲', -1);

-- alice 答的问卷
INSERT INTO `answer` (`user_id`, `questionnaire_id`) VALUES (1, 1);

-- bob 答的问卷
INSERT INTO `answer` (`user_id`, `questionnaire_id`) VALUES (2, 3);

-- carol 答的问卷
INSERT INTO `answer` (`user_id`, `questionnaire_id`) VALUES (3, 2);

-- admin 答的问卷
INSERT INTO `answer` (`user_id`, `questionnaire_id`) VALUES (4, 1);

-- alice 答卷 (answer_id = 1), 答案选偏内向、直觉、逻辑、计划
INSERT INTO `answer_detail` (`answer_id`, `question_id`, `option_id`)
VALUES (1, 1, 2), (1, 2, 3), (1, 3, 5), (1, 4, 7);

-- bob 答卷 (answer_id = 2), 答案选偏外向、大局、感觉、随意
INSERT INTO `answer_detail` (`answer_id`, `question_id`, `option_id`)
VALUES (2, 9, 6), (2, 10, 10), (2, 11, 12), (2, 12, 14);

-- carol 答卷 (answer_id = 3), 中性偏直觉、逻辑、计划
INSERT INTO `answer_detail` (`answer_id`, `question_id`, `option_id`)
VALUES (3, 5, 4), (3, 6, 11), (3, 7, 13), (3, 8, 15);

-- admin 答卷 (answer_id = 4), 偏社交、现实、感性、灵活
INSERT INTO `answer_detail` (`answer_id`, `question_id`, `option_id`)
VALUES (4, 1, 1), (4, 2, 4), (4, 3, 6), (4, 4, 8);





