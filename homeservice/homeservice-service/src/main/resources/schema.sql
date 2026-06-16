CREATE TABLE IF NOT EXISTS `service_category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(255) COMMENT '分类描述',
  `icon` VARCHAR(255) COMMENT '图标URL',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_sort (`sort`),
  INDEX idx_status (`status`),
  UNIQUE KEY uk_name (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

CREATE TABLE IF NOT EXISTS `service_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务项目ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `description` TEXT COMMENT '服务描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `unit` VARCHAR(20) DEFAULT '次' COMMENT '计价单位',
  `duration` INT DEFAULT 60 COMMENT '服务时长（分钟）',
  `images` JSON COMMENT '服务图片',
  `tags` JSON COMMENT '标签',
  `requirements` TEXT COMMENT '服务要求',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_category_id (`category_id`),
  INDEX idx_status (`status`),
  UNIQUE KEY uk_category_name (`category_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

INSERT IGNORE INTO `service_category` (`name`, `description`, `icon`, `sort`, `status`) VALUES
('家庭清洁', '专业的家庭清洁服务', 'clean', 1, 1),
('保姆月嫂', '专业的保姆和月嫂服务', 'nanny', 2, 1),
('家电维修', '各类家电维修服务', 'repair', 3, 1),
('管道疏通', '专业的管道疏通服务', 'pipe', 4, 1),
('搬家服务', '专业的搬家服务', 'move', 5, 1);

INSERT IGNORE INTO `service_item` (`category_id`, `name`, `description`, `price`, `unit`, `duration`, `sort`, `status`) VALUES
(1, '日常保洁', '家庭日常清洁服务', 50.00, '次', 120, 1, 1),
(1, '深度保洁', '家庭深度清洁服务', 150.00, '次', 240, 2, 1),
(1, '开荒保洁', '新房开荒清洁服务', 300.00, '次', 480, 3, 1),
(2, '住家保姆', '全天候保姆服务', 5000.00, '月', 0, 1, 1),
(2, '钟点工', '按小时计费的保姆服务', 30.00, '小时', 60, 2, 1),
(3, '空调维修', '空调维修和清洗服务', 100.00, '次', 60, 1, 1),
(3, '冰箱维修', '冰箱维修服务', 80.00, '次', 60, 2, 1),
(4, '下水道疏通', '下水道疏通服务', 100.00, '次', 60, 1, 1),
(4, '马桶疏通', '马桶疏通服务', 80.00, '次', 60, 2, 1),
(5, '小型搬家', '小型搬家服务', 300.00, '次', 240, 1, 1),
(5, '大型搬家', '大型搬家服务', 800.00, '次', 480, 2, 1);