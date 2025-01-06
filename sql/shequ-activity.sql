/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : shequ-activity

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 06/01/2025 16:46:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity_info
-- ----------------------------
DROP TABLE IF EXISTS `activity_info`;
CREATE TABLE `activity_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动id',
  `activity_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
  `activity_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '活动类型（1：满减，2：折扣）',
  `activity_desc` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动描述',
  `start_time` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` date NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for activity_rule
-- ----------------------------
DROP TABLE IF EXISTS `activity_rule`;
CREATE TABLE `activity_rule`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_id` int(11) NULL DEFAULT NULL COMMENT '类型',
  `activity_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '活动类型（1：满减，2：折扣）',
  `condition_amount` decimal(16, 2) NULL DEFAULT NULL COMMENT '满减金额',
  `condition_num` bigint(20) NULL DEFAULT NULL COMMENT '满减件数',
  `benefit_amount` decimal(16, 2) NULL DEFAULT NULL COMMENT '优惠金额',
  `benefit_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠折扣',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for activity_sku
-- ----------------------------
DROP TABLE IF EXISTS `activity_sku`;
CREATE TABLE `activity_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_id` bigint(20) NULL DEFAULT NULL COMMENT '活动id ',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '活动参与商品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for coupon_info
-- ----------------------------
DROP TABLE IF EXISTS `coupon_info`;
CREATE TABLE `coupon_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '购物券类型 1 现金券 2 满减券',
  `coupon_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠卷名字',
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `condition_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛 0->没门槛',
  `start_time` date NULL DEFAULT NULL COMMENT '可以领取的开始日期',
  `end_time` date NULL DEFAULT NULL COMMENT '可以领取的结束日期',
  `range_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '使用范围[1->全场通用；2->指定分类；3->指定商品]',
  `range_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用范围描述',
  `publish_count` int(11) NOT NULL DEFAULT 1 COMMENT '发行数量',
  `per_limit` int(11) NOT NULL DEFAULT 1 COMMENT '每人限领张数',
  `use_count` int(11) NOT NULL DEFAULT 0 COMMENT '已使用数量',
  `receive_count` int(11) NOT NULL DEFAULT 0 COMMENT '领取数量',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `publish_status` tinyint(1) NULL DEFAULT NULL COMMENT '发布状态[0-未发布，1-已发布]',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠券信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for coupon_info_1
-- ----------------------------
DROP TABLE IF EXISTS `coupon_info_1`;
CREATE TABLE `coupon_info_1`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_type` tinyint(1) NULL DEFAULT NULL COMMENT '优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]',
  `coupon_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠卷名字',
  `num` int(11) NULL DEFAULT NULL COMMENT '数量',
  `amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '金额',
  `per_limit` int(11) NULL DEFAULT NULL COMMENT '每人限领张数',
  `condition_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '使用门槛 0->没门槛',
  `start_time` datetime NULL DEFAULT NULL COMMENT '使用开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '使用结束时间',
  `range_type` tinyint(1) NULL DEFAULT NULL COMMENT '使用范围[0->全场通用；1->指定分类；2->指定商品]',
  `range_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用范围描述',
  `publish_count` int(11) NULL DEFAULT NULL COMMENT '发行数量',
  `use_count` int(11) NULL DEFAULT NULL COMMENT '已使用数量',
  `receive_count` int(11) NULL DEFAULT NULL COMMENT '领取数量',
  `enable_start_time` datetime NULL DEFAULT NULL COMMENT '可以领取的开始日期',
  `enable_end_time` datetime NULL DEFAULT NULL COMMENT '可以领取的结束日期',
  `publish_status` tinyint(1) NULL DEFAULT NULL COMMENT '发布状态[0-未发布，1-已发布]',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 1 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠券信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for coupon_range
-- ----------------------------
DROP TABLE IF EXISTS `coupon_range`;
CREATE TABLE `coupon_range`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物券编号',
  `coupon_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '优惠券id',
  `range_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '范围类型； 1->商品(sku) ；2->分类',
  `range_id` bigint(20) NOT NULL DEFAULT 0,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠券范围表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for coupon_use
-- ----------------------------
DROP TABLE IF EXISTS `coupon_use`;
CREATE TABLE `coupon_use`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT '购物券ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单ID',
  `coupon_status` tinyint(3) NULL DEFAULT NULL COMMENT '购物券状态（1：未使用 2：已使用）',
  `get_type` tinyint(3) NOT NULL DEFAULT 2 COMMENT '获取类型（1：后台赠送；2：主动获取）',
  `get_time` datetime NULL DEFAULT NULL COMMENT '获取时间',
  `using_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `used_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠券领用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for home_subject
-- ----------------------------
DROP TABLE IF EXISTS `home_subject`;
CREATE TABLE `home_subject`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专题名字',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专题标题',
  `sub_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专题副标题',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '显示状态',
  `url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详情连接',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `img` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专题图片地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 1 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for home_subject_sku
-- ----------------------------
DROP TABLE IF EXISTS `home_subject_sku`;
CREATE TABLE `home_subject_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专题名字',
  `subject_id` bigint(20) NULL DEFAULT NULL COMMENT '专题id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 1 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '专题商品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动标题',
  `start_time` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` date NULL DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '上下线状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '秒杀活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seckill_sku
-- ----------------------------
DROP TABLE IF EXISTS `seckill_sku`;
CREATE TABLE `seckill_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `seckill_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '秒杀活动id',
  `seckill_time_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '活动场次id',
  `sku_id` bigint(20) NOT NULL DEFAULT 0 COMMENT 'skuId',
  `sku_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sku名称',
  `img_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `seckill_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '秒杀价格',
  `seckill_stock` decimal(10, 0) NULL DEFAULT NULL COMMENT '秒杀库存',
  `seckill_limit` decimal(10, 0) NULL DEFAULT NULL COMMENT '每人限购数量',
  `seckill_sale` int(11) NOT NULL DEFAULT 0 COMMENT '秒杀销量',
  `seckill_sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '秒杀活动商品关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seckill_sku_notice
-- ----------------------------
DROP TABLE IF EXISTS `seckill_sku_notice`;
CREATE TABLE `seckill_sku_notice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT 'user_id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `session_id` bigint(20) NULL DEFAULT NULL COMMENT '活动场次id',
  `subcribe_time` datetime NULL DEFAULT NULL COMMENT '订阅时间',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `notice_type` tinyint(1) NULL DEFAULT NULL COMMENT '通知方式[0-短信，1-邮件]',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '秒杀商品通知订阅' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seckill_time
-- ----------------------------
DROP TABLE IF EXISTS `seckill_time`;
CREATE TABLE `seckill_time`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '场次名称',
  `start_time` time NULL DEFAULT NULL COMMENT '每日开始时间',
  `end_time` time NULL DEFAULT NULL COMMENT '每日结束时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '启用状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '秒杀活动场次' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sku_info
-- ----------------------------
DROP TABLE IF EXISTS `sku_info`;
CREATE TABLE `sku_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '分类id',
  `attr_group_id` bigint(1) NOT NULL DEFAULT 0 COMMENT '平台属性分组id',
  `sku_types` tinyint(1) NOT NULL DEFAULT 0 COMMENT '商品类型：0->普通商品 1->秒杀商品',
  `sku_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'sku名称',
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '展示图片',
  `per_limit` int(11) NOT NULL DEFAULT 1 COMMENT '限购个数/每天（0：不限购）',
  `publish_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '上架状态：0->下架；1->上架',
  `check_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '审核状态：0->未审核；1->审核通过',
  `is_new_person` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否新人专享：0->否；1->是',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `sku_code` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sku编码',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `market_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '市场价',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存',
  `lock_stock` int(11) NOT NULL DEFAULT 0 COMMENT '锁定库存',
  `low_stock` int(11) NOT NULL DEFAULT 0 COMMENT '预警库存',
  `sale` int(11) NOT NULL DEFAULT 0 COMMENT '销量',
  `ware_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '仓库',
  `version` bigint(20) NOT NULL DEFAULT 0,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'sku信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
