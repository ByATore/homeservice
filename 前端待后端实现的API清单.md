# 前端已实现但后端未实现的接口清单

> 本文档整理了小程序前端已有功能中，后端尚未提供 API 支持的部分，供后端开发参考。

---

## 一、首页（pages/index）

### 1.1 Banner 轮播管理

| 项目 | 说明 |
|------|------|
| **前端现状** | 硬编码了 3 张轮播图（图片 + 标题 + 描述 + 按钮），仅用于展示 |
| **缺失 API** | 无 Banner 管理接口 |
| **建议实现** | |

**新增实体**: `banner` 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| title | varchar(100) | 标题 |
| description | varchar(500) | 描述文字 |
| image_url | varchar(500) | 图片地址 |
| link_type | tinyint | 跳转类型：1=服务详情, 2=预约页, 3=外部链接 |
| link_value | varchar(500) | 跳转目标值 |
| sort | int | 排序 |
| status | tinyint | 状态：0=禁用, 1=启用 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

**建议 API**:
- `GET /api/catalog/banner/list` — 获取所有启用中的 Banner（按 sort 排序）

---

### 1.2 热门服务推荐

| 项目 | 说明 |
|------|------|
| **前端现状** | 硬编码 3 个服务卡片，展示服务名、价格、预约人数、用户头像等 |
| **缺失 API** | 后端有 `GET /order/top-services` 但返回的是统计数据，没有预约人数和头像 |
| **建议实现** | |

**建议扩展现有 API**:
- 扩展 `GET /api/order/top-services` 返回数据，增加字段：
  - `bookingCount` — 预约人数
  - `recentAvatars` — 最近预约用户头像列表
  - `tag` — 标签文字（如"口碑爆款"、"深度养护"）
  - `originalPrice` — 原价

---

### 1.3 城市/位置选择

| 项目 | 说明 |
|------|------|
| **前端现状** | 顶部显示"北京市"，硬编码，点击弹 Toast |
| **缺失 API** | 无城市列表、当前城市定位 API |
| **建议实现** | |

**建议 API**:
- `GET /api/catalog/city/hot` — 获取热门城市列表
- `GET /api/catalog/city/list` — 获取全部城市列表（按字母分组）

**新增实体**: `city` 表（可选，也可直接硬编码城市数据）

---

### 1.4 搜索功能

| 项目 | 说明 |
|------|------|
| **前端现状** | 搜索栏点击弹 Toast，未实现搜索 |
| **缺失 API** | 无服务搜索接口 |
| **建议实现** | |

**建议 API**:
- `GET /api/service/item/search?keyword=xxx` — 按关键词搜索服务项目

可通过扩展现有 `ServiceItemController` 的分页查询，增加 `keyword` 参数模糊匹配 `name` 和 `description` 字段实现。

---

### 1.5 优惠券弹窗

| 项目 | 说明 |
|------|------|
| **前端现状** | 首页弹出优惠券领取弹窗（¥20券），点击后显示领取成功撒花动画。弹窗数据完全硬编码 |
| **缺失 API** | 1. 无"获取可领取优惠券"API<br>2. 无"用户领取优惠券"API<br>3. 后端有 `GET /api/marketing/coupon/list` 但只是管理端查询所有有效优惠券 |
| **建议实现** | |

**新增实体**: `user_coupon` 表（用户—优惠券关联）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| coupon_id | bigint | 优惠券ID |
| status | tinyint | 状态：0=未使用, 1=已使用, 2=已过期 |
| received_at | datetime | 领取时间 |
| used_at | datetime | 使用时间 |
| expire_time | datetime | 过期时间 |

**建议 API**:
- `GET /api/marketing/coupon/available` — 获取当前用户可领取的优惠券
- `POST /api/marketing/coupon/{id}/receive` — 用户领取优惠券
- `GET /api/marketing/coupon/my` — 获取当前用户已领取的优惠券
- `GET /api/marketing/coupon/my/available` — 获取当前用户可用（未使用+未过期）优惠券

---

## 二、服务详情页（pages/service-detail）

### 2.1 服务范围

| 项目 | 说明 |
|------|------|
| **前端现状** | 硬编码 4 个服务范围卡片（客餐厅、卧室、厨房、卫生间），每个有 icon + 名称 + 描述 |
| **缺失 API** | 无服务范围接口 |
| **建议实现** | |

**方案一**: 在 `service_item` 表增加 `scope` 字段（JSON 类型），存储服务范围列表。

**方案二**: 新建 `service_scope` 表，与 `service_item` 多对多关联。

---

### 2.2 服务流程

| 项目 | 说明 |
|------|------|
| **前端现状** | 硬编码 4 个步骤（在线预约 → 匹配阿姨 → 上门服务 → 验收评价） |
| **缺失 API** | 无服务流程接口 |
| **建议实现** | 服务流程为通用内容，可作为系统配置存入 `config` 表，通过 `GET /api/config/public` 获取。<br>或直接在 `service_item` 表增加 `process_steps` 字段（JSON 类型）。 |

---

### 2.3 评价评分汇总

| 项目 | 说明 |
|------|------|
| **前端现状** | 显示"4.9分"，硬编码。评价列表已对接 `GET /api/review/page` |
| **缺失 API** | 无评价汇总统计 API（平均分、评价总数） |
| **建议实现** | |

**建议 API**:
- `GET /api/review/summary?serviceItemId=xxx` — 获取指定服务的评价汇总
  
返回：`{ averageRating: 4.9, totalCount: 128, ratingDistribution: {5: 80, 4: 30, 3: 12, 2: 4, 1: 2} }`

---

### 2.4 服务详情 Hero 图

| 项目 | 说明 |
|------|------|
| **前端现状** | 硬编码 Unsplash 图片 URL |
| **缺失 API** | ServiceItem 有 `images` 字段，但前端未从 API 数据中读取展示。需确认 `images` 字段存储的是单个 URL 还是 JSON 数组 |
| **建议实现** | 前端已支持 `serviceItem` 绑定，只需确保 `images` 字段返回首张图片 URL 即可 |

---

## 三、预约页（pages/booking）

### 3.1 可预约时间段查询

| 项目 | 说明 |
|------|------|
| **前端现状** | 前端自行生成 7 天日期 + 每天 9 个时间段（08:00-17:00），固定逻辑标记 08:00 为"约满" |
| **缺失 API** | 无时间段可用性查询接口 |

**建议实现**:

**新增实体**: `service_schedule` 表（服务排班）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| service_item_id | bigint | 服务项目ID |
| date | date | 日期 |
| time_slot | varchar(10) | 时间段（如 09:00） |
| max_capacity | int | 最大容量 |
| booked_count | int | 已预约数 |
| status | tinyint | 状态 |

**建议 API**:
- `GET /api/order/timeslots?serviceItemId=xxx&date=2024-01-15` — 获取指定日期可用时间段
- `GET /api/order/available-dates?serviceItemId=xxx&days=7` — 获取未来 N 天可预约日期

---

### 3.2 优惠券选择

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"选择优惠券"弹 Toast，费用明细中的优惠券展示也是硬编码（-¥20.00） |
| **缺失 API** | 需要用户可用优惠券列表 API（见 1.5），下单时支持传入优惠券 ID 并计算折扣金额 |
| **建议实现** | 依赖 1.5 的用户优惠券 API，下单接口 `POST /api/order` 增加 `couponId` 字段支持 |

---

### 3.3 地址选择

| 项目 | 说明 |
|------|------|
| **前端现状** | 使用了 `wx.chooseLocation` 微信原生地图选址，但手动输入地址和地址管理功能缺失 |
| **缺失 API** | 无用户地址管理接口 |
| **建议实现** | |

**新增实体**: `user_address` 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| contact_name | varchar(50) | 联系人 |
| contact_phone | varchar(20) | 联系电话 |
| province | varchar(50) | 省 |
| city | varchar(50) | 市 |
| district | varchar(50) | 区 |
| detail | varchar(200) | 详细地址 |
| latitude | decimal(10,7) | 纬度 |
| longitude | decimal(10,7) | 经度 |
| is_default | tinyint | 是否默认 |
| created_at | datetime | 创建时间 |

**建议 API**:
- `GET /api/user/address/list` — 获取当前用户地址列表
- `POST /api/user/address` — 新增地址
- `PUT /api/user/address/{id}` — 更新地址
- `DELETE /api/user/address/{id}` — 删除地址
- `PUT /api/user/address/{id}/default` — 设为默认地址

---

## 四、订单页（pages/orders）

### 4.1 订单详情

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"查看详情"跳转到服务详情页，而非订单详情页 |
| **缺失页面** | 前端没有订单详情页，后端已有 `GET /api/order/{id}` 接口 |
| **建议实现** | 前端新增订单详情页，或复用服务详情页并传递订单参数 |

### 4.2 支付功能

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"立即付款"弹 Toast |
| **缺失 API** | 后端有 `PaymentController`（支付 CRUD），但缺少微信支付对接 |
| **建议实现** | |

**建议 API**:
- `POST /api/payment/wxpay` — 发起微信支付（返回支付参数给前端调起微信支付）
- `POST /api/payment/wxpay/notify` — 微信支付回调通知

---

### 4.3 删除订单

| 项目 | 说明 |
|------|------|
| **前端现状** | 已完成订单显示"删除订单"，前端本地删除，未调用后端 |
| **缺失 API** | 后端 `OrderController` 无单个删除接口，仅有 `DELETE /batch` 批量删除 |
| **建议实现** | 增加 `DELETE /api/order/{id}` 接口，做软删除（更新 status） |

---

## 五、个人中心（pages/profile）

### 5.1 我的优惠券

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"我的优惠券"弹 Toast，菜单显示 badge "2" |
| **缺失 API** | 见 1.5，需用户优惠券列表 API |
| **建议实现** | 依赖 1.5 |

### 5.2 服务地址管理

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"服务地址"弹 Toast |
| **缺失 API** | 见 3.3，需用户地址 CRUD API |
| **建议实现** | 依赖 3.3 |

### 5.3 我的收藏

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"我的收藏"弹 Toast |
| **缺失 API** | 无收藏功能 |
| **建议实现** | |

**新增实体**: `user_favorite` 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| service_item_id | bigint | 服务项目ID |
| created_at | datetime | 收藏时间 |

**建议 API**:
- `GET /api/user/favorite/list` — 获取收藏列表（含服务详情）
- `POST /api/user/favorite` — 收藏服务
- `DELETE /api/user/favorite/{serviceItemId}` — 取消收藏
- `GET /api/user/favorite/check/{serviceItemId}` — 检查是否已收藏

### 5.4 联系客服

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"联系客服"弹 Toast |
| **缺失 API** | 无客服联系方式配置 |
| **建议实现** | 通过系统配置 `GET /api/config/public?key=customer_service` 返回客服电话/微信/二维码等 |

### 5.5 关于我们

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"关于我们"弹 Toast |
| **缺失 API** | 无关于我们内容接口 |
| **建议实现** | 通过系统配置 `GET /api/config/public?key=about_us` 返回富文本内容 |

---

## 六、登录页（pages/login）

### 6.1 微信一键登录

| 项目 | 说明 |
|------|------|
| **前端现状** | 点击"微信一键登录"弹 Toast（开发中） |
| **缺失 API** | 无微信登录接口 |

**建议实现**:

**建议 API**:
- `POST /api/auth/wechat-login` — 微信登录

请求参数:
```json
{
  "code": "wx.login() 返回的code",
  "nickname": "用户昵称（可选）",
  "avatarUrl": "头像URL（可选）"
}
```
后端通过 code 调用微信接口 `jscode2session` 换取 `openid` 和 `session_key`，然后查找或创建用户，返回 token。

---

## 七、通用功能

### 7.1 消息通知

| 项目 | 说明 |
|------|------|
| **前端现状** | 无通知功能入口 |
| **缺失 API** | 后端有 `NotifyController`，但前端未对接 |
| **建议实现** | 前端可增加通知中心入口，对接 `GET /api/notify/list` |

### 7.2 系统配置

| 项目 | 说明 |
|------|------|
| **前端现状** | 未对接 |
| **缺失 API** | 后端有 `ConfigController` / `ConfigPublicController` |
| **建议实现** | 通过 `GET /api/config/public?key=xxx` 获取各类前端所需的配置项（如关于我们、客服电话等） |

---

## 汇总优先级

### P0（核心缺失，影响主流程）

| 编号 | 功能 | 涉及页面 |
|------|------|---------|
| 1 | 微信一键登录 | 登录页 |
| 2 | 支付功能（微信支付） | 订单页 |
| 3 | 用户地址管理 | 预约页、个人中心 |

### P1（重要缺失，影响用户体验）

| 编号 | 功能 | 涉及页面 |
|------|------|---------|
| 4 | Banner 管理 | 首页 |
| 5 | 搜索功能 | 首页 |
| 6 | 评价评分汇总 | 服务详情 |
| 7 | 可预约时间段查询 | 预约页 |
| 8 | 用户优惠券领取/查看 | 首页、预约页、个人中心 |
| 9 | 我的收藏 | 个人中心 |

### P2（体验优化）

| 编号 | 功能 | 涉及页面 |
|------|------|---------|
| 10 | 城市/位置选择 | 首页 |
| 11 | 服务范围（后端存储） | 服务详情 |
| 12 | 服务流程（后端存储） | 服务详情 |
| 13 | 联系客服配置 | 个人中心 |
| 14 | 关于我们配置 | 个人中心 |
| 15 | 热门服务推荐优化 | 首页 |

---

*文档生成日期：2026-06-16*