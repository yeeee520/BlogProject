-- ========================================
-- 个人博客数据库初始化脚本
-- ========================================

-- 选择数据库
USE blog;

-- ========================================
-- 1. 用户表
-- ========================================
CREATE TABLE IF NOT EXISTS `user` (
    `user_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`   VARCHAR(50)  NOT NULL                COMMENT '用户名（唯一，显示用）',
    `name`       VARCHAR(50)  NOT NULL                COMMENT '用户账号（唯一，登录用）',
    `password`   VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt加密后存储）',
    `token`      VARCHAR(500) DEFAULT NULL            COMMENT 'JWT令牌（登录成功后记录，用于黑名单/续签）',
    `fans`       INT          NOT NULL DEFAULT 0      COMMENT '粉丝数',
    `focus`      INT          NOT NULL DEFAULT 0      COMMENT '关注数',
    `photo`      VARCHAR(255) DEFAULT NULL            COMMENT '头像URL',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 2. 文章表
-- ========================================
CREATE TABLE IF NOT EXISTS `article` (
    `article_id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `user_id`      BIGINT       NOT NULL                COMMENT '作者ID',
    `title`        VARCHAR(200) NOT NULL                COMMENT '文章标题',
    `content`      LONGTEXT     NOT NULL                COMMENT '文章内容（Markdown格式）',
    `state`        TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0=草稿, 1=我们, 2=游记, 3=计划',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `view_count`   INT          NOT NULL DEFAULT 0      COMMENT '浏览数量',
    `like_count`   INT          NOT NULL DEFAULT 0      COMMENT '点赞数量',
    PRIMARY KEY (`article_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_state` (`state`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ========================================
-- 3. 图片表
-- ========================================
CREATE TABLE IF NOT EXISTS `image` (
    `image_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `url`         VARCHAR(500) NOT NULL                COMMENT '图片链接URL',
    `article_id`  BIGINT       DEFAULT NULL            COMMENT '关联文章ID（可为空）',
    `upload_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`image_id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';

-- ========================================
-- 4. 评论表
-- ========================================
CREATE TABLE IF NOT EXISTS `comment` (
    `comment_id`  BIGINT   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `article_id`  BIGINT   NOT NULL                COMMENT '文章ID',
    `suser_id`    BIGINT   NOT NULL                COMMENT '评论作者ID',
    `username`    VARCHAR(50) NOT NULL             COMMENT '评论作者用户名',
    `content`     TEXT     NOT NULL                COMMENT '评论内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `like_count`  INT      NOT NULL DEFAULT 0      COMMENT '点赞数量',
    PRIMARY KEY (`comment_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_suser_id` (`suser_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ========================================
-- 5. 用户点赞关联表
-- （替代 user 表中的 user_like_article 字段，
--   点赞关系用单独表存储，支持幂等与高效查询）
-- ========================================
CREATE TABLE IF NOT EXISTS `user_article_like` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT   NOT NULL                COMMENT '点赞用户ID',
    `article_id`  BIGINT   NOT NULL                COMMENT '被点赞文章ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞文章关联表';

-- ========================================
-- 查看建表结果
-- ========================================
SHOW TABLES;
