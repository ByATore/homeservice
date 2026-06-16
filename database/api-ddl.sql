-- ============================================
-- 家政服务系统 — API 实现所需 DDL
-- 生成日期：2026-06-16
-- ============================================

-- ============================================
-- User 表扩展（微信登录相关）
-- ============================================
ALTER TABLE `user`
  ADD COLUMN `openid` VARCHAR(100) COMMENT '微信openid' AFTER `balance`,
  ADD COLUMN `nickname` VARCHAR(100) COMMENT '微信昵称' AFTER `openid`,
  ADD UNIQUE INDEX `uk_openid` (`openid`);

-- ============================================
-- user_address 表
-- ============================================
CREATE TABLE IF NOT EXISTS `user_address` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `contact_name` VARCHAR(50) COMMENT '联系人',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `province` VARCHAR(50) COMMENT '省',
  `city` VARCHAR(50) COMMENT '市',
  `district` VARCHAR(50) COMMENT '区',
  `detail` VARCHAR(200) COMMENT '详细地址',
  `latitude` DECIMAL(10,7) COMMENT '纬度',
  `longitude` DECIMAL(10,7) COMMENT '经度',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- ============================================
-- user_favorite 表
-- ============================================
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `service_item_id` BIGINT NOT NULL COMMENT '服务项目ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  INDEX `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_user_service` (`user_id`, `service_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ============================================
-- service_schedule 表（可预约时间段）
-- ============================================
CREATE TABLE IF NOT EXISTS `service_schedule` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '排班ID',
  `service_item_id` BIGINT NOT NULL COMMENT '服务项目ID',
  `date` DATE NOT NULL COMMENT '日期',
  `time_slot` VARCHAR(10) NOT NULL COMMENT '时间段（如 09:00）',
  `max_capacity` INT DEFAULT 5 COMMENT '最大容量',
  `booked_count` INT DEFAULT 0 COMMENT '已预约数',
  `status` TINYINT DEFAULT 1 COMMENT '0=禁用 1=启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_service_date_time` (`service_item_id`, `date`, `time_slot`),
  INDEX `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务排班时间段表';
