-- ============================================
-- 数据库性能优化迁移脚本
-- 功能：
--   1. 移除所有物理外键约束 (FOREIGN KEY)
--   2. 改为逻辑关联（仅通过字段值和索引关联）
--   3. 添加覆盖索引优化查询性能
--   4. 添加逻辑关联查询视图
-- ============================================

USE homeservice;

-- ============================================
-- 第一步：移除物理外键约束
-- ============================================

-- worker 表：移除 user_id 外键
ALTER TABLE `worker` DROP FOREIGN KEY IF EXISTS `worker_ibfk_1`;

-- service_item 表：移除 category_id 外键
ALTER TABLE `service_item` DROP FOREIGN KEY IF EXISTS `service_item_ibfk_1`;

-- order 表：移除 user_id, worker_id, service_item_id 外键
ALTER TABLE `order` DROP FOREIGN KEY IF EXISTS `order_ibfk_1`;
ALTER TABLE `order` DROP FOREIGN KEY IF EXISTS `order_ibfk_2`;
ALTER TABLE `order` DROP FOREIGN KEY IF EXISTS `order_ibfk_3`;

-- payment 表：移除 order_id, user_id 外键
ALTER TABLE `payment` DROP FOREIGN KEY IF EXISTS `payment_ibfk_1`;
ALTER TABLE `payment` DROP FOREIGN KEY IF EXISTS `payment_ibfk_2`;

-- review 表：移除 order_id, user_id, worker_id, service_item_id 外键
ALTER TABLE `review` DROP FOREIGN KEY IF EXISTS `review_ibfk_1`;
ALTER TABLE `review` DROP FOREIGN KEY IF EXISTS `review_ibfk_2`;
ALTER TABLE `review` DROP FOREIGN KEY IF EXISTS `review_ibfk_3`;
ALTER TABLE `review` DROP FOREIGN KEY IF EXISTS `review_ibfk_4`;

-- user_coupon 表：移除 user_id, coupon_id, order_id 外键
ALTER TABLE `user_coupon` DROP FOREIGN KEY IF EXISTS `user_coupon_ibfk_1`;
ALTER TABLE `user_coupon` DROP FOREIGN KEY IF EXISTS `user_coupon_ibfk_2`;
ALTER TABLE `user_coupon` DROP FOREIGN KEY IF EXISTS `user_coupon_ibfk_3`;


-- ============================================
-- 第二步：添加逻辑关联所需的覆盖索引
-- ============================================

-- order 表：添加复合索引优化常见查询
-- 用于分页查询：用户订单列表
CREATE INDEX IF NOT EXISTS idx_order_user_status_time 
    ON `order`(`user_id`, `status`, `created_at`);

-- 用于分页查询：服务人员订单列表
CREATE INDEX IF NOT EXISTS idx_order_worker_status_time 
    ON `order`(`worker_id`, `status`, `created_at`);

-- 用于统计查询：按日期+状态聚合（替代原有的全表扫描 GROUP BY）
CREATE INDEX IF NOT EXISTS idx_order_created_status 
    ON `order`(`created_at`, `status`);

-- 用于订单趋势查询的覆盖索引
CREATE INDEX IF NOT EXISTS idx_order_trend 
    ON `order`(`created_at`, `status`, `actual_amount`);

-- payment 表：添加复合索引
CREATE INDEX IF NOT EXISTS idx_payment_order_user 
    ON `payment`(`order_id`, `user_id`);

-- review 表：添加复合索引优化评价查询
CREATE INDEX IF NOT EXISTS idx_review_worker_time 
    ON `review`(`worker_id`, `created_at` DESC);

CREATE INDEX IF NOT EXISTS idx_review_order_user 
    ON `review`(`order_id`, `user_id`);

-- user_coupon 表：添加复合索引
CREATE INDEX IF NOT EXISTS idx_user_coupon_user_status 
    ON `user_coupon`(`user_id`, `status`);

-- worker 表：添加快照字段，避免 JOIN 查用户名
ALTER TABLE `worker` 
    ADD COLUMN IF NOT EXISTS `user_real_name` VARCHAR(50) COMMENT '用户真实姓名(快照)' AFTER `user_id`,
    ADD COLUMN IF NOT EXISTS `user_phone` VARCHAR(20) COMMENT '用户手机号(快照)' AFTER `user_real_name`;

-- 从现有数据回填快照字段
UPDATE `worker` w 
INNER JOIN `user` u ON w.user_id = u.id 
SET w.user_real_name = u.real_name, w.user_phone = u.phone;

-- review 表：添加冗余字段，避免多表 JOIN
ALTER TABLE `review` 
    ADD COLUMN IF NOT EXISTS `user_real_name` VARCHAR(50) COMMENT '评价用户姓名(快照)' AFTER `user_id`,
    ADD COLUMN IF NOT EXISTS `worker_real_name` VARCHAR(50) COMMENT '服务人员姓名(快照)' AFTER `worker_id`,
    ADD COLUMN IF NOT EXISTS `service_name` VARCHAR(100) COMMENT '服务名称(快照)' AFTER `service_item_id`;