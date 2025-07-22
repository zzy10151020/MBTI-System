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
  `score` TINYINT NOT NULL CHECK (score BETWEEN -10 AND 10),
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

-- 插入用戶數據，密碼雜湊值符合您的 Java 加密邏輯
-- 密碼分別為: admin123, alice123, bob123, carol123
INSERT INTO `user` (`username`, `password_hash`, `email`, `role`)
VALUES 
  ('admin', 'SpPN0PPJm5lOlS13xiUye8ZRk6cAvHGHBcR9dUZcN94=:VM1iX73wvxTTAj4li8Dv2c948QEuWbP4Q7jGKeN19eU=', 'admin@example.com', 'admin'),
  ('alice', 'YChy5y+w88G2sJO4Ukr63N3pN7LojurID9Zeuv4VXQU=:E4/IQ4kTZUIaz2IVUIsBQFf4rDDpZoxdjL1t1gi+R6c=', 'alice@example.com', 'user'),
  ('bob', '8HW8c+xoVh5VpJEcYrfLgJBaPfozcW6KtkIKI3yK1/0=:p/hg2g3xJa1QCYte9CDuKMmKpxt0dHJ7eGv6XYPSQ0Q=', 'bob@example.com', 'user'),
  ('carol', '8E4AeTz+H576HIXbez9/I3qut0eVeYEctxrMrYRyKvI=:QCRj6G4PWYhnmEZxPB0OZx4HTugNk2GIbCCR4/ovMAc=', 'carol@example.com', 'user');

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
(2, '依赖直觉', -1), (2, '依赖现实', 1),
(6, '相信直觉', -1), (6, '看重经验', 1),
(10, '关注大局', -1), (10, '关注细节', 1);

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

-- 1. 創建一份新的問卷
INSERT INTO `questionnaire` (`title`, `description`, `creator_id`, `is_published`) VALUES
('MBTI 性格測試', '本測驗旨在幫助您更好地了解自己的性格特質，請根據您的真實想法作答。', 1, FALSE);

-- 2. 獲取剛創建的問卷ID
SET @questionnaire_id = LAST_INSERT_ID();

-- =================================================================
-- Questions and Options Data
-- =================================================================

-- Question 1
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在一个熟悉的环境中工作还是在一个新鲜的环境中工作？', 'S/N', 1);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '熟悉的环境', 5),
(@question_id, '新鲜的环境', -5);

-- Question 2
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢参加一个大型的聚会还是一个小型的聚会？', 'E/I', 2);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '大型的聚会', 7),
(@question_id, '小型的聚会', -7);

-- Question 3
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢主动地与他人交流还是被动地等待他人来找你？', 'E/I', 3);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '主动地与他人交流', 8),
(@question_id, '被动地等待他人来找你', -8);

-- Question 4
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在做决定之前听取他人的意见还是依靠自己的判断？', 'T/F', 4);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '听取他人的意见', -6),
(@question_id, '依靠自己的判断', 6);

-- Question 5
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在空闲时间做一些有趣的活动还是做一些放松的活动？', 'E/I', 5);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有趣的活动', 5),
(@question_id, '放松的活动', -5);

-- Question 6
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢与不同类型的人交往还是与相似类型的人交往？', 'E/I', 6);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '不同类型的人', 6),
(@question_id, '相似类型的人', -6);

-- Question 7
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在团队中担任领导者还是担任执行者？', 'E/I', 7);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '领导者', 7),
(@question_id, '执行者', -7);

-- Question 8
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在公开场合表达自己的想法还是在私密场合表达自己的想法？', 'E/I', 8);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '公开场合', 8),
(@question_id, '私密场合', -8);

-- Question 9
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在学习或工作时有一些背景音乐还是保持安静？', 'E/I', 9);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有一些背景音乐', 4),
(@question_id, '保持安静', -4);

-- Question 10
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢通过电话或视频通话与他人沟通还是通过文字或邮件与他人沟通？', 'E/I', 10);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '电话或视频通话', 7),
(@question_id, '文字或邮件', -7);

-- Question 11
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更倾向于通过思考还是感受来做决策？', 'T/F', 11);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '思考', 9),
(@question_id, '感受', -9);

-- Question 12
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢遵循规则和逻辑还是遵循价值和情感？', 'T/F', 12);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '规则和逻辑', 8),
(@question_id, '价值和情感', -8);

-- Question 13
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在处理问题时客观还是主观？', 'T/F', 13);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '客观', 8),
(@question_id, '主观', -8);

-- Question 14
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在与他人相处时直接还是委婉？', 'T/F', 14);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '直接', 6),
(@question_id, '委婉', -6);

-- Question 15
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在工作或学习中追求效率还是和谐？', 'T/F', 15);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '效率', 7),
(@question_id, '和谐', -7);

-- Question 16
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在面对冲突时坚持自己的立场还是寻求妥协？', 'T/F', 16);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '坚持自己的立场', 7),
(@question_id, '寻求妥协', -7);

-- Question 17
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在给别人建议时提供理性的分析还是提供情感的支持？', 'T/F', 17);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '理性的分析', 8),
(@question_id, '情感的支持', -8);

-- Question 18
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在评价自己或他人时以标准为依据还是以情况为依据？', 'T/F', 18);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '以标准为依据', 7),
(@question_id, '以情况为依据', -7);

-- Question 19
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在做事情时有一定的步骤和顺序还是有一定的灵活性和自由度？', 'J/P', 19);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有一定的步骤和顺序', 8),
(@question_id, '有一定的灵活性和自由度', -8);

-- Question 20
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在选择朋友时以共同的兴趣为基础还是以共同的价值为基础？', 'T/F', 20);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '共同的兴趣', -5),
(@question_id, '共同的价值', 5);

-- Question 21
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢从细节入手还是从大局出发？', 'S/N', 21);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '从细节入手', 8),
(@question_id, '从大局出发', -8);

-- Question 22
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢处理具体的事实还是抽象的概念？', 'S/N', 22);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '具体的事实', 9),
(@question_id, '抽象的概念', -9);

-- Question 23
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢依赖经验还是依赖直觉？', 'S/N', 23);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '依赖经验', 7),
(@question_id, '依赖直觉', -7);

-- Question 24
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢按照现实的情况来安排自己的生活还是按照自己的想象来安排自己的生活？', 'S/N', 24);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '按照现实的情况', 8),
(@question_id, '按照自己的想象', -8);

-- Question 25
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢遵循已有的方法还是创造新的方法？', 'S/N', 25);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '遵循已有的方法', 6),
(@question_id, '创造新的方法', -6);

-- Question 26
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢关注事物的现状还是关注事物的发展？', 'S/N', 26);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '关注事物的现状', 7),
(@question_id, '关注事物的发展', -7);

-- Question 27
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在学习或工作中重复已经掌握的内容还是探索未知的内容？', 'S/N', 27);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '重复已经掌握的内容', 6),
(@question_id, '探索未知的内容', -6);

-- Question 28
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在思考问题时遵循常识还是打破常规？', 'S/N', 28);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '遵循常识', 7),
(@question_id, '打破常规', -7);

-- Question 29
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在做事情时有明确的目标和计划还是有开放的可能性和机会？', 'J/P', 29);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有明确的目标和计划', 9),
(@question_id, '有开放的可能性和机会', -9);

-- Question 30
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在选择兴趣爱好时以实用性为考虑因素还是以创造性为考虑因素？', 'S/N', 30);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '以实用性为考虑因素', 6),
(@question_id, '以创造性为考虑因素', -6);

-- Question 31
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '在完成任务时，你更倾向于哪种工作模式？', 'J/P', 31);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '按照既定的工作计划和流程执行', 8),
(@question_id, '根据实际情况灵活调整工作方式', -8);

-- Question 32
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在做决定时尽早确定还是尽量推迟？', 'J/P', 32);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '尽早确定', 9),
(@question_id, '尽量推迟', -9);

-- Question 33
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在安排自己的时间时有一个明确的计划还是随心所欲？', 'J/P', 33);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有一个明确的计划', 10),
(@question_id, '随心所欲', -10);

-- Question 34
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在面对变化时做好准备还是随机应变？', 'J/P', 34);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '做好准备', 8),
(@question_id, '随机应变', -8);

-- Question 35
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在处理事情时有条理和组织还是有杂乱和混乱？', 'J/P', 35);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有条理和组织', 9),
(@question_id, '有杂乱和混乱', -9);

-- Question 36
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在完成任务时遵循已有的标准还是创造自己的标准？', 'J/P', 36);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '遵循已有的标准', 7),
(@question_id, '创造自己的标准', -7);

-- Question 37
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在与他人合作时遵守约定还是保持弹性？', 'J/P', 37);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '遵守约定', 8),
(@question_id, '保持弹性', -8);

-- Question 38
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在学习或工作中专注于一个领域还是涉猎多个领域？', 'S/N', 38);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '专注于一个领域', 6),
(@question_id, '涉猎多个领域', -6);

-- Question 39
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在做事情时按照自己的意志还是按照外界的期待？', 'T/F', 39);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '按照自己的意志', 5),
(@question_id, '按照外界的期待', -5);

-- Question 40
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你更喜欢在选择事物时以个人的喜好为依据还是以社会的规范为依据？', 'T/F', 40);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '以个人的喜好为依据', -7),
(@question_id, '以社会的规范为依据', 7);

-- Question 41
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在一个社交活动中的表现方式：', 'E/I', 41);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '主动参与，与多数人交流，享受气氛', 9),
(@question_id, '被动观察，与少数人交流，感觉累', -9);

-- Question 42
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在学习或工作时的偏好方式：', 'E/I', 42);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢与他人合作，讨论问题，分享想法', 7),
(@question_id, '喜欢独自工作，思考问题，保留想法', -7);

-- Question 43
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在遇到困难时的求助方式：', 'E/I', 43);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '倾向于向他人寻求帮助，寻找外部资源', 6),
(@question_id, '倾向于自己解决问题，寻找内部资源', -6);

-- Question 44
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在休闲时间的活动方式：', 'E/I', 44);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢参加各种活动，结识新朋友，拓展社交圈', 8),
(@question_id, '喜欢做一些安静的事情，享受独处，维持社交圈', -8);

-- Question 45
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在表达自己的想法时的沟通方式：', 'E/I', 45);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢口头表达，直接说出，不怕冒险', 7),
(@question_id, '喜欢书面表达，事先准备，谨慎小心', -7);

-- Question 46
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在接收信息时的注意力倾向：', 'E/I', 46);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '关注外部环境，接收多样化的信息，易受干扰', 6),
(@question_id, '关注内部世界，筛选有用的信息，不易受干扰', -6);

-- Question 47
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在充电时的恢复方式：', 'E/I', 47);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '通过与他人交流，获得能量和动力', 9),
(@question_id, '通过独自思考，获得平静和安宁', -9);

-- Question 48
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在认识自己时的了解方式：', 'E/I', 48);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '通过他人的反馈，了解自己的优缺点', 5),
(@question_id, '通过自我反省，了解自己的优缺点', -5);

-- Question 49
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对变化时的适应方式：', 'S/N', 49);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '乐于接受变化，寻找新鲜感', -7),
(@question_id, '抵制变化，寻找稳定感', 7);

-- Question 50
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在建立信任时的信赖方式：', 'S/N', 50);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '通过频繁的互动，建立信任关系', 6),
(@question_id, '通过深入的了解，建立信任关系', -6);

-- Question 51
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在学习新知识时的偏好方式：', 'S/N', 51);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢通过理解和联想来学习，探索原理和规律', -8),
(@question_id, '喜欢通过记忆和练习来学习，掌握事实和技能', 8);

-- Question 52
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在解决问题时的思维方式：', 'S/N', 52);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢从不同角度思考，寻找多种解决方案', -7),
(@question_id, '喜欢按照常规思路，寻找最直接的解决方案', 7);

-- Question 53
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在做决策时的依据：', 'S/N', 53);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更多考虑可能性和潜在影响', -8),
(@question_id, '更多考虑现实情况和实际效果', 8);

-- Question 54
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对压力时的表现：', 'T/F', 54);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '变得更加积极主动，迎接挑战', 6),
(@question_id, '变得更加谨慎小心，避免出错', -6);

-- Question 55
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在选择职业时的考虑因素：', 'T/F', 55);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更注重个人兴趣和成就感', -7),
(@question_id, '更注重稳定性和社会地位', 7);

-- Question 56
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在与他人发生冲突时的处理方式：', 'T/F', 56);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '直接表达自己的观点，争取说服对方', 8),
(@question_id, '先倾听对方的意见，寻求妥协的可能', -8);

-- Question 57
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在评价他人时的侧重点：', 'T/F', 57);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更关注他人的行为和成就', 7),
(@question_id, '更关注他人的性格和品质', -7);

-- Question 58
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对失败时的反应：', 'T/F', 58);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '把失败看作是学习和成长的机会', 6),
(@question_id, '对失败感到沮丧，怀疑自己的能力', -6);

-- Question 59
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在生活中的消费习惯：', 'T/F', 59);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更倾向于理性消费，注重性价比', 8),
(@question_id, '更倾向于感性消费，注重个人喜好', -8);

-- Question 60
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对不确定的事情时的态度：', 'J/P', 60);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '感到兴奋，愿意尝试和探索', -8),
(@question_id, '感到不安，希望获得更多信息', 8);

-- Question 61
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在团队中的角色定位：', 'J/P', 61);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更倾向于提出新的想法和创意', -7),
(@question_id, '更倾向于组织和协调团队工作', 7);

-- Question 62
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在学习或工作中的动力来源：', 'T/F', 62);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '内在的兴趣和热情', -6),
(@question_id, '外在的奖励和认可', 6);

-- Question 63
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在处理人际关系时的原则：', 'T/F', 63);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '以真诚和善良为基础', -8),
(@question_id, '以公平和正义为基础', 8);

-- Question 64
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对困难任务时的心态：', 'T/F', 64);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '相信自己能够克服困难', 7),
(@question_id, '担心自己无法完成任务', -7);

-- Question 65
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在选择居住环境时的偏好：', 'E/I', 65);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢热闹繁华的城市', 7),
(@question_id, '喜欢宁静舒适的乡村', -7);

-- Question 66
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对新的挑战时的准备程度：', 'J/P', 66);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '通常会提前做好充分准备', 9),
(@question_id, '更喜欢在实践中边做边学', -9);

-- Question 67
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在与他人合作时的沟通频率：', 'E/I', 67);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '经常与团队成员沟通交流', 7),
(@question_id, '只在必要时与团队成员沟通', -7);

-- Question 68
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在表达情感时的方式：', 'T/F', 68);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '比较直接和坦率', 6),
(@question_id, '比较含蓄和委婉', -6);

-- Question 69
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对批评时的态度：', 'T/F', 69);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '虚心接受，认真反思', -7),
(@question_id, '容易感到委屈，进行辩解', 7);

-- Question 70
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在做事情时的坚持程度：', 'J/P', 70);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '一旦决定，就会坚持到底', 8),
(@question_id, '根据实际情况，适时调整', -8);

-- Question 71
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在选择娱乐活动时的偏好：', 'E/I', 71);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢参与竞技性的活动', 6),
(@question_id, '喜欢参与放松性的活动', -6);

-- Question 72
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对未知领域时的好奇心：', 'S/N', 72);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '非常强烈，渴望探索', -8),
(@question_id, '比较一般，不太主动', 8);

-- Question 73
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在团队中的合作风格：', 'T/F', 73);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更注重团队的整体目标', -7),
(@question_id, '更注重个人的发挥空间', 7);

-- Question 74
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在处理日常事务时的效率：', 'J/P', 74);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '通常能够高效完成任务', 8),
(@question_id, '有时会拖延，效率不高', -8);

-- Question 75
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对选择时的纠结程度：', 'J/P', 75);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '比较容易做出决定', 8),
(@question_id, '经常会犹豫不决', -8);

-- Question 76
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在与他人交往时的主动性：', 'E/I', 76);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '总是主动与他人建立联系', 9),
(@question_id, '通常等待他人主动与自己联系', -9);

-- Question 77
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对压力时的应对策略：', 'J/P', 77);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '通过运动或其他方式释放压力', -7),
(@question_id, '通过休息或调整心态缓解压力', 7);

-- Question 78
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在学习或工作中的创新能力：', 'S/N', 78);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '经常能够提出新的想法和方法', -8),
(@question_id, '更擅长运用已有的经验和方法', 8);

-- Question 79
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在生活中的计划安排：', 'J/P', 79);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '喜欢制定详细的计划，并严格执行', 10),
(@question_id, '更喜欢灵活安排，根据实际情况调整', -10);

-- Question 80
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对竞争时的态度：', 'T/F', 80);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '积极参与，努力争取胜利', 7),
(@question_id, '尽量避免，不太喜欢竞争', -7);

-- Question 81
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在与他人沟通时的耐心程度：', 'T/F', 81);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '非常有耐心，认真倾听他人意见', -8),
(@question_id, '有时会缺乏耐心，急于表达自己观点', 8);

-- Question 82
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对挫折时的恢复速度：', 'T/F', 82);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '能够很快从挫折中恢复过来', 6),
(@question_id, '需要较长时间才能从挫折中走出来', -6);

-- Question 83
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在选择朋友时的标准：', 'T/F', 83);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更看重朋友的性格和品德', -7),
(@question_id, '更看重朋友的兴趣和爱好', 7);

-- Question 84
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在做决策时的果断程度：', 'J/P', 84);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '能够迅速做出决策', 8),
(@question_id, '需要较长时间思考后才能做出决策', -8);

-- Question 85
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对变化时的适应速度：', 'J/P', 85);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '能够快速适应新的变化', -8),
(@question_id, '需要一定时间来适应新的变化', 8);

-- Question 86
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在团队中的影响力：', 'E/I', 86);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '能够积极影响团队成员，推动团队发展', 8),
(@question_id, '更多是跟随团队成员，适应团队氛围', -8);

-- Question 87
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在生活中的社交圈子：', 'E/I', 87);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '比较广泛，认识很多不同类型的人', 8),
(@question_id, '比较狭窄，只与少数人保持密切联系', -8);

-- Question 88
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对困难时的求助对象：', 'E/I', 88);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '更倾向于向家人和朋友求助', 6),
(@question_id, '更倾向于自己独立解决', -6);

-- Question 89
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在学习或工作中的专注程度：', 'E/I', 89);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '能够长时间保持专注', -7),
(@question_id, '容易受到外界干扰，注意力不集中', 7);

-- Question 90
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对风险时的态度：', 'S/N', 90);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '愿意尝试高风险高回报的事情', -8),
(@question_id, '更倾向于选择低风险的事情', 8);

-- Question 91
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在与他人交往时的真诚程度：', 'T/F', 91);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '始终保持真诚，不虚伪做作', -7),
(@question_id, '有时会根据情况适当掩饰自己', 7);

-- Question 92
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在生活中的兴趣爱好：', 'S/N', 92);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '比较多样化，涉及多个领域', -7),
(@question_id, '相对单一，集中在某几个领域', 7);

-- Question 93
INSERT INTO `question` (`questionnaire_id`, `content`, `dimension`, `question_order`) VALUES
(@questionnaire_id, '你在面对未来的规划：', 'J/P', 93);
SET @question_id = LAST_INSERT_ID();
INSERT INTO `option` (`question_id`, `content`, `score`) VALUES
(@question_id, '有明确的目标和详细的计划', 10),
(@question_id, '没有明确的规划，走一步看一步', -10);