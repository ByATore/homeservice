-- ============================================
-- 家政服务系统 — 后端重构 DDL 脚本
-- 生成日期：2026-06-16
-- 说明：按 Phase 顺序执行，所有操作可回滚
-- ============================================

-- ============================================
-- Phase 1: 支付融合 —— order 表扩展
-- ============================================

ALTER TABLE `order`
  ADD COLUMN `payment_no` VARCHAR(50) COMMENT '支付流水号',
  ADD COLUMN `payment_method` TINYINT COMMENT '支付方式：1=微信 2=支付宝',
  ADD COLUMN `transaction_id` VARCHAR(100) COMMENT '第三方交易号',
  ADD COLUMN `paid_at` DATETIME COMMENT '支付时间',
  ADD COLUMN `refund_amount` DECIMAL(10,2) COMMENT '退款金额',
  ADD COLUMN `refund_at` DATETIME COMMENT '退款时间',
  ADD COLUMN `refund_reason` VARCHAR(500) COMMENT '退款原因',
  ADD UNIQUE INDEX `uk_payment_no` (`payment_no`),
  ADD INDEX `idx_transaction_id` (`transaction_id`);

-- ============================================
-- Phase 1: 支付表归档
-- ============================================

RENAME TABLE `payment` TO `payment_archive`;

-- 数据迁移（如果 payment_archive 中有数据）
UPDATE `order` o
INNER JOIN `payment_archive` p ON o.id = p.order_id
SET
  o.payment_no = p.payment_no,
  o.payment_method = p.payment_method,
  o.transaction_id = p.transaction_id,
  o.paid_at = p.pay_time,
  o.refund_amount = p.refund_amount,
  o.refund_at = p.refund_time,
  o.refund_reason = p.refund_reason;

-- ============================================
-- Phase 3: media_file 表
-- ============================================

CREATE TABLE IF NOT EXISTS `media_file` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
  `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_key` VARCHAR(500) NOT NULL COMMENT '存储Key/路径',
  `file_url` VARCHAR(500) NOT NULL COMMENT '访问URL',
  `thumbnail_url` VARCHAR(500) COMMENT '缩略图URL',
  `file_type` VARCHAR(20) COMMENT '文件类型: image/video/file',
  `mime_type` VARCHAR(100) COMMENT 'MIME类型',
  `file_size` BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
  `width` INT COMMENT '图片宽度(px)',
  `height` INT COMMENT '图片高度(px)',
  `storage_type` VARCHAR(20) DEFAULT 'local' COMMENT '存储类型: local/oss/cos/minio',
  `module` VARCHAR(50) COMMENT '关联模块: banner/service/avatar/decoration',
  `module_id` BIGINT COMMENT '关联业务ID',
  `uploader_id` BIGINT COMMENT '上传者ID',
  `status` TINYINT DEFAULT 1 COMMENT '0=已删除 1=正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_file_key` (`file_key`),
  INDEX `idx_module` (`module`, `module_id`),
  INDEX `idx_uploader` (`uploader_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体文件表';

-- ============================================
-- Phase 3: banner 表
-- ============================================

CREATE TABLE IF NOT EXISTS `banner` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Banner ID',
  `title` VARCHAR(100) COMMENT '标题',
  `subtitle` VARCHAR(200) COMMENT '副标题',
  `description` VARCHAR(500) COMMENT '描述文字',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片地址',
  `image_file_id` BIGINT COMMENT '关联 media_file.id',
  `link_type` TINYINT DEFAULT 1 COMMENT '1=服务详情 2=外部链接 3=分类页 4=预约页 5=不跳转',
  `link_value` VARCHAR(500) COMMENT '跳转目标值',
  `position` VARCHAR(50) DEFAULT 'home' COMMENT '展示位置: home=首页',
  `sort` INT DEFAULT 0 COMMENT '排序（越小越靠前）',
  `status` TINYINT DEFAULT 1 COMMENT '0=禁用 1=启用',
  `start_time` DATETIME COMMENT '投放开始时间',
  `end_time` DATETIME COMMENT '投放结束时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_position_status_time` (`position`, `status`, `start_time`, `end_time`),
  INDEX `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner轮播图表';

-- ============================================
-- Phase 3: decoration_component 表
-- ============================================

CREATE TABLE IF NOT EXISTS `decoration_component` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '组件ID',
  `name` VARCHAR(100) NOT NULL COMMENT '组件名称',
  `page` VARCHAR(50) NOT NULL COMMENT '页面: home/category/profile',
  `section` VARCHAR(50) COMMENT '区块: top_banner/hot_service/icon_nav/coupon_bar',
  `component_type` VARCHAR(50) NOT NULL COMMENT '组件类型: banner/service_card/icon_grid/text_block',
  `title` VARCHAR(100) COMMENT '组件展示标题',
  `config_json` JSON COMMENT '组件配置数据(JSON，前端按type解析)',
  `image_url` VARCHAR(500) COMMENT '配图',
  `image_file_id` BIGINT COMMENT '关联 media_file.id',
  `link_type` TINYINT DEFAULT 0 COMMENT '跳转类型',
  `link_value` VARCHAR(500) COMMENT '跳转目标',
  `sort` INT DEFAULT 0 COMMENT '同 section 内排序',
  `status` TINYINT DEFAULT 1 COMMENT '0=禁用 1=启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_page_section_sort` (`page`, `section`, `sort`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小程序装修组件表';

-- ============================================
-- sys_config 配置项
-- ============================================

INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`, `status`) VALUES
-- 支付配置
('payment.wechat.appid', '', 'TEXT', '微信支付AppId', 1),
('payment.wechat.mchid', '', 'TEXT', '微信支付商户号', 1),
('payment.wechat.api_key', '', 'TEXT', '微信支付API密钥', 1),
('payment.wechat.notify_url', '', 'TEXT', '微信支付回调URL', 1),
('payment.alipay.appid', '', 'TEXT', '支付宝AppId', 1),
('payment.alipay.notify_url', '', 'TEXT', '支付宝回调URL', 1),
('payment.timeout.minutes', '30', 'NUMBER', '支付超时时间(分钟)', 1),

-- 地图配置
('map.sdk.type', 'tencent', 'TEXT', '地图SDK类型: tencent/amap', 1),
('map.tencent.key', '', 'TEXT', '腾讯地图Key', 1),
('city.hot.list', '["北京","上海","广州","深圳","杭州","成都"]', 'JSON', '热门城市列表', 1),

-- 存储配置
('storage.type', 'local', 'TEXT', '存储类型: local/oss/cos/minio', 1),
('storage.custom_domain', '', 'TEXT', '自定义CDN域名', 1),
('storage.local.upload_path', '/data/homeservice/uploads', 'TEXT', '本地存储路径', 1),
('storage.local.access_path', '/uploads', 'TEXT', '本地访问路径前缀', 1),
('storage.oss.endpoint', 'https://oss-cn-hangzhou.aliyuncs.com', 'TEXT', 'OSS Endpoint', 1),
('storage.oss.bucket', 'homeservice-images', 'TEXT', 'OSS Bucket', 1),
('storage.oss.access_key', '', 'TEXT', 'OSS AccessKey', 1),
('storage.oss.secret_key', '', 'TEXT', 'OSS SecretKey', 1),

-- 图片处理配置
('image.thumbnail.width', '200', 'NUMBER', '缩略图宽度', 1),
('image.thumbnail.height', '200', 'NUMBER', '缩略图高度', 1),
('image.max_size_mb', '10', 'NUMBER', '上传最大大小(MB)', 1),
('image.allowed_types', 'jpg,jpeg,png,gif,webp', 'TEXT', '允许的图片类型', 1);
