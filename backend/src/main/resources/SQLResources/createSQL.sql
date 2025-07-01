-- 创建数据库
CREATE DATABASE IF NOT EXISTS mbti_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mbti_test;

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

-- 插入示例数据
-- 1. 创建管理员和普通用户
INSERT INTO `user` (`username`, `password_hash`, `email`, `role`) VALUES
('admin', '$2y$10$ExampleHashAdmin', 'admin@mbti.com', 'admin'),
('user1', '$2y$10$ExampleHashUser1', 'user1@test.com', 'user'),
('user2', '$2y$10$ExampleHashUser2', 'user2@test.com', 'user');

-- 2. 创建问卷
INSERT INTO `questionnaire` (`title`, `description`, `creator_id`) VALUES
('标准MBTI测试', '经典93题MBTI人格测试', 1),
('简易版测试', '快速28题人格评估', 1);

-- 3. 添加测试问题（以标准测试的第一个问题为例）
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(1, '当你参加社交活动时，你通常', 'E/I', 1),
(1, '你更倾向于通过哪种方式获取信息', 'S/N', 2),
(1, '做决定时，你主要依据', 'T/F', 3),
(1, '你更喜欢什么样的生活方式', 'J/P', 4);

-- 4. 添加问题选项
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
-- 问题1的选项
(1, '主动认识新朋友', 1),   -- E倾向
(1, '等待别人先打招呼', -1), -- I倾向

-- 问题2的选项
(2, '具体的事实和细节', -1), -- S倾向
(2, '总体的概念和可能性', 1), -- N倾向

-- 问题3的选项
(3, '逻辑分析和客观标准', -1), -- T倾向
(3, '个人价值观和情感影响', 1), -- F倾向

-- 问题4的选项
(4, '有计划有组织的方式', -1), -- J倾向
(4, '灵活随性的方式', 1);     -- P倾向

-- 5. 添加用户回答记录
INSERT INTO `answer` (`user_id`, `questionnaire_id`) VALUES
(2, 1),
(3, 1);

-- 6. 添加回答详情
INSERT INTO `answer_detail` (`answer_id`, `question_id`, `option_id`) VALUES
-- 用户2的回答
(1, 1, 1),  -- 选择"主动认识新朋友"(E)
(1, 2, 3),  -- 选择"具体的事实和细节"(S)
(1, 3, 6),  -- 选择"个人价值观和情感影响"(F)
(1, 4, 7),  -- 选择"灵活随性的方式"(P)

-- 用户3的回答
(2, 1, 2),  -- 选择"等待别人先打招呼"(I)
(2, 2, 4);  -- 选择"总体的