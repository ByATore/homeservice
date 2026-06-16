CREATE TABLE IF NOT EXISTS `statistics` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_users` INT DEFAULT 0 COMMENT '总用户数',
  `new_users` INT DEFAULT 0 COMMENT '新增用户数',
  `total_workers` INT DEFAULT 0 COMMENT '总服务人员数',
  `new_workers` INT DEFAULT 0 COMMENT '新增服务人员数',
  `total_orders` INT DEFAULT 0 COMMENT '总订单数',
  `new_orders` INT DEFAULT 0 COMMENT '新增订单数',
  `completed_orders` INT DEFAULT 0 COMMENT '完成订单数',
  `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总金额',
  `total_income` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总收入',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_stat_date (`stat_date`),
  INDEX idx_stat_date (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计数据表';