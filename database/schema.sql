-- 家政上门服务系统数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS homeservice DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE homeservice;

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` DATE COMMENT '生日',
  `address` VARCHAR(255) COMMENT '地址',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `user_type` TINYINT DEFAULT 1 COMMENT '用户类型：1-普通用户，2-服务人员，3-管理员',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_phone (`phone`),
  INDEX idx_email (`email`),
  INDEX idx_user_type (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 服务人员表
CREATE TABLE `worker` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务人员ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_real_name` VARCHAR(50) COMMENT '用户真实姓名(快照)',
  `user_phone` VARCHAR(20) COMMENT '用户手机号(快照)',
  `worker_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '工号',
  `id_card` VARCHAR(20) COMMENT '身份证号',
  `education` VARCHAR(50) COMMENT '学历',
  `experience_years` INT DEFAULT 0 COMMENT '从业年限',
  `specialty` VARCHAR(255) COMMENT '专长',
  `self_introduction` TEXT COMMENT '自我介绍',
  `rating` DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分',
  `service_count` INT DEFAULT 0 COMMENT '服务次数',
  `total_earnings` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计收入',
  `certificates` JSON COMMENT '证书信息',
  `work_areas` JSON COMMENT '服务区域',
  `available_time` JSON COMMENT '可服务时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-忙碌，3-休假',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_worker_no (`worker_no`),
  INDEX idx_status (`status`),
  INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务人员表';

-- 服务分类表
CREATE TABLE `service_category` (
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

-- 服务项目表
CREATE TABLE `service_item` (
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

-- 订单表
CREATE TABLE `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `worker_id` BIGINT COMMENT '服务人员ID',
  `service_item_id` BIGINT NOT NULL COMMENT '服务项目ID',
  `service_name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `service_price` DECIMAL(10,2) NOT NULL COMMENT '服务价格',
  `service_time` DATETIME COMMENT '预约服务时间',
  `service_address` VARCHAR(255) COMMENT '服务地址',
  `service_duration` INT DEFAULT 60 COMMENT '服务时长（分钟）',
  `contact_name` VARCHAR(50) COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) COMMENT '联系人电话',
  `remark` TEXT COMMENT '备注',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `coupon_id` BIGINT COMMENT '优惠券ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-待支付，2-待服务，3-服务中，4-待评价，5-已完成，6-已取消，7-已退款',
  `cancel_reason` VARCHAR(255) COMMENT '取消原因',
  `cancel_time` DATETIME COMMENT '取消时间',
  `start_time` DATETIME COMMENT '开始服务时间',
  `end_time` DATETIME COMMENT '结束服务时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_no (`order_no`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_worker_id (`worker_id`),
  INDEX idx_status (`status`),
  INDEX idx_service_time (`service_time`),
  INDEX idx_order_user_status_time (`user_id`, `status`, `created_at`),
  INDEX idx_order_worker_status_time (`worker_id`, `status`, `created_at`),
  INDEX idx_order_created_status (`created_at`, `status`),
  INDEX idx_order_trend (`created_at`, `status`, `actual_amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 支付记录表
CREATE TABLE `payment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
  `payment_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '支付流水号',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `payment_method` TINYINT NOT NULL COMMENT '支付方式：1-微信支付，2-支付宝，3-余额支付',
  `payment_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `transaction_id` VARCHAR(100) COMMENT '第三方交易流水号',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-待支付，2-支付成功，3-支付失败，4-已退款',
  `pay_time` DATETIME COMMENT '支付时间',
  `refund_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额',
  `refund_time` DATETIME COMMENT '退款时间',
  `refund_reason` VARCHAR(255) COMMENT '退款原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_payment_no (`payment_no`),
  INDEX idx_order_id (`order_id`),
  INDEX idx_order_no (`order_no`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_status (`status`),
  INDEX idx_payment_order_user (`order_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 评价表
CREATE TABLE `review` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_real_name` VARCHAR(50) COMMENT '评价用户姓名(快照)',
  `worker_id` BIGINT NOT NULL COMMENT '服务人员ID',
  `worker_real_name` VARCHAR(50) COMMENT '服务人员姓名(快照)',
  `service_item_id` BIGINT NOT NULL COMMENT '服务项目ID',
  `service_name` VARCHAR(100) COMMENT '服务名称(快照)',
  `rating` TINYINT NOT NULL COMMENT '评分：1-5星',
  `content` TEXT COMMENT '评价内容',
  `images` JSON COMMENT '评价图片',
  `tags` JSON COMMENT '评价标签',
  `reply` TEXT COMMENT '商家回复',
  `reply_time` DATETIME COMMENT '回复时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_id (`order_id`),
  INDEX idx_worker_id (`worker_id`),
  INDEX idx_rating (`rating`),
  INDEX idx_status (`status`),
  INDEX idx_review_worker_time (`worker_id`, `created_at`),
  INDEX idx_review_order_user (`order_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 优惠券表
CREATE TABLE `coupon` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `description` VARCHAR(255) COMMENT '优惠券描述',
  `type` TINYINT NOT NULL COMMENT '类型：1-满减券，2-折扣券，3-立减券',
  `discount_amount` DECIMAL(10,2) COMMENT '优惠金额',
  `discount_rate` DECIMAL(5,2) COMMENT '折扣率',
  `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费金额',
  `max_discount` DECIMAL(10,2) COMMENT '最大优惠金额',
  `total_quantity` INT DEFAULT 0 COMMENT '发行总量',
  `used_quantity` INT DEFAULT 0 COMMENT '已使用数量',
  `valid_days` INT COMMENT '有效天数',
  `start_time` DATETIME COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_type (`type`),
  INDEX idx_status (`status`),
  INDEX idx_time (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表
CREATE TABLE `user_coupon` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户优惠券ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID',
  `order_id` BIGINT COMMENT '订单ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-未使用，2-已使用，3-已过期',
  `used_time` DATETIME COMMENT '使用时间',
  `expire_time` DATETIME COMMENT '过期时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_coupon_id (`coupon_id`),
  INDEX idx_status (`status`),
  INDEX idx_user_coupon_user_status (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 统计数据表
CREATE TABLE `statistics` (
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

-- Seata AT 模式 undo_log 表（分布式事务回滚日志）
CREATE TABLE `undo_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '自增ID',
  `branch_id` BIGINT NOT NULL COMMENT '分支事务ID',
  `xid` VARCHAR(100) NOT NULL COMMENT '全局事务ID',
  `context` VARCHAR(128) NOT NULL COMMENT 'undo_log上下文',
  `rollback_info` LONGBLOB NOT NULL COMMENT '回滚信息',
  `log_status` INT NOT NULL COMMENT '日志状态',
  `log_created` DATETIME NOT NULL COMMENT '创建时间',
  `log_modified` DATETIME NOT NULL COMMENT '修改时间',
  UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Seata分布式事务回滚日志表';

-- 本地消息表（分布式事务最终一致性）
CREATE TABLE `mq_message` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  `message_id` VARCHAR(64) UNIQUE NOT NULL COMMENT '消息唯一标识',
  `exchange` VARCHAR(100) NOT NULL COMMENT '交换机',
  `routing_key` VARCHAR(100) NOT NULL COMMENT '路由键',
  `message_body` JSON NOT NULL COMMENT '消息体',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待发送，1-发送成功，2-发送失败',
  `retry_count` INT DEFAULT 0 COMMENT '重试次数',
  `max_retry` INT DEFAULT 5 COMMENT '最大重试次数',
  `next_retry_time` DATETIME COMMENT '下次重试时间',
  `error_msg` VARCHAR(500) COMMENT '错误信息',
  `business_type` VARCHAR(50) COMMENT '业务类型',
  `business_id` VARCHAR(64) COMMENT '业务ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_message_id (`message_id`),
  INDEX idx_status_next_retry (`status`, `next_retry_time`),
  INDEX idx_business (`business_type`, `business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表';

-- 系统配置表
CREATE TABLE `sys_config` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) UNIQUE NOT NULL COMMENT '配置键',
  `config_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `config_value` TEXT COMMENT '配置值',
  `config_type` TINYINT DEFAULT 1 COMMENT '配置类型：1-文本，2-JSON，3-数字，4-布尔',
  `config_group` VARCHAR(50) DEFAULT 'default' COMMENT '配置分组',
  `remark` VARCHAR(255) COMMENT '备注',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_config_key (`config_key`),
  INDEX idx_config_group (`config_group`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化数据
INSERT INTO `service_category` (`name`, `description`, `icon`, `sort`, `status`) VALUES
('家庭清洁', '专业的家庭清洁服务', 'clean', 1, 1),
('保姆月嫂', '专业的保姆和月嫂服务', 'nanny', 2, 1),
('家电维修', '各类家电维修服务', 'repair', 3, 1),
('管道疏通', '专业的管道疏通服务', 'pipe', 4, 1),
('搬家服务', '专业的搬家服务', 'move', 5, 1);

INSERT INTO `service_item` (`category_id`, `name`, `description`, `price`, `unit`, `duration`, `sort`, `status`) VALUES
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

-- 插入管理员账户（密码: lw211014ly）
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `real_name`, `user_type`, `status`) VALUES
('admin', '$2b$12$FdsNwAE/dVhoVmQR16ry7.QInGa19ER2Gvyq/aF6gkfZ/Mol4Fwty', '13800138000', 'admin@homeservice.com', '系统管理员', 3, 1);

-- 初始化系统配置数据
INSERT INTO `sys_config` (`config_key`, `config_name`, `config_value`, `config_type`, `config_group`, `remark`, `status`) VALUES
('upload.domain', '上传域名', 'https://upload.homeservice.com', 1, 'upload', '文件上传服务域名', 1),
('upload.max_file_size', '上传文件大小限制', '10', 3, 'upload', '上传文件大小限制(MB)', 1),
('upload.allowed_types', '允许上传文件类型', 'jpg,jpeg,png,gif,pdf,doc,docx', 1, 'upload', '允许上传的文件类型，逗号分隔', 1),
('payment.wechat.app_id', '微信支付AppID', 'wx1234567890abcdef', 1, 'payment', '微信支付商户AppID', 1),
('payment.wechat.mch_id', '微信支付商户号', '1234567890', 3, 'payment', '微信支付商户号', 1),
('payment.wechat.api_key', '微信支付API密钥', '', 1, 'payment', '微信支付API密钥', 1),
('payment.wechat.notify_url', '微信支付回调地址', 'https://api.homeservice.com/api/v1/pay/callback/wechat', 1, 'payment', '微信支付异步通知地址', 1),
('payment.alipay.app_id', '支付宝AppID', '2021001234567890', 1, 'payment', '支付宝应用ID', 1),
('payment.alipay.private_key', '支付宝应用私钥', '', 1, 'payment', '支付宝应用私钥', 1),
('payment.alipay.public_key', '支付宝公钥', '', 1, 'payment', '支付宝公钥', 1),
('payment.alipay.notify_url', '支付宝回调地址', 'https://api.homeservice.com/api/v1/pay/callback/alipay', 1, 'payment', '支付宝异步通知地址', 1),
('sms.provider', '短信服务商', 'aliyun', 1, 'sms', '短信服务商：aliyun/tencent', 1),
('sms.access_key', '短信AccessKey', '', 1, 'sms', '短信服务AccessKey', 1),
('sms.secret_key', '短信SecretKey', '', 1, 'sms', '短信服务SecretKey', 1),
('sms.sign_name', '短信签名', '家政上门服务', 1, 'sms', '短信签名', 1),
('sms.template_code', '短信模板Code', '', 1, 'sms', '短信模板Code', 1),
('map.api_key', '地图API密钥', '', 1, 'map', '地图服务(高德/腾讯)API密钥', 1),
('system.app_name', '应用名称', '家政上门服务', 1, 'system', '应用名称', 1),
('system.app_version', '应用版本', '1.0.0', 1, 'system', '应用版本号', 1),
('system.customer_service_phone', '客服电话', '400-123-4567', 1, 'system', '客服电话', 1);