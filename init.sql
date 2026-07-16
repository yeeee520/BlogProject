-- ========================================
-- 个人博客数据库初始化脚本
-- 版本: v2.0 (2026-07-16)
-- 说明: 这是数据库的唯一真理源。
--       本地和服务器的数据库表结构都必须从此文件导出。
--       修改实体类后，先更新此文件，再提交 Git。
-- 用法: mysql -u root -p < init.sql
--       此脚本可重复执行（CREATE TABLE IF NOT EXISTS），不会损坏已有数据。
-- ========================================

-- 如果你使用的是服务器（blog 数据库），取消下面这行的注释：
-- USE blog;
-- 如果你使用的是本地开发（code2026 数据库），保持下面这行：
USE code2026;

-- ========================================
-- 1. 用户表
-- ========================================
CREATE TABLE IF NOT EXISTS user (
    user_id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username   VARCHAR(50)  NOT NULL                COMMENT '用户名（唯一，显示用）',
    
ame       VARCHAR(50)  NOT NULL                COMMENT '用户账号（唯一，登录用）',
    password   VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt加密后存储）',
    	oken      VARCHAR(500) DEFAULT NULL            COMMENT 'JWT令牌（登录成功后记录，用于黑名单/续签）',
    ans       INT          NOT NULL DEFAULT 0      COMMENT '粉丝数',
    ocus      INT          NOT NULL DEFAULT 0      COMMENT '关注数',
    photo      VARCHAR(255) DEFAULT NULL            COMMENT '头像URL',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_name (
ame)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 2. 文章表
-- ========================================
CREATE TABLE IF NOT EXISTS rticle (
    rticle_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    user_id      BIGINT       NOT NULL                COMMENT '作者ID',
    	itle        VARCHAR(200) NOT NULL                COMMENT '文章标题',
    content      LONGTEXT     NOT NULL                COMMENT '文章内容（Markdown格式）',
    state        TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0=草稿, 1=我们, 2=游记, 3=计划',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    iew_count   INT          NOT NULL DEFAULT 0      COMMENT '浏览数量',
    like_count   INT          NOT NULL DEFAULT 0      COMMENT '点赞数量',
    PRIMARY KEY (rticle_id),
    KEY idx_user_id (user_id),
    KEY idx_state (state),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ========================================
-- 3. 图片表
-- ========================================
CREATE TABLE IF NOT EXISTS image (
    image_id    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    url         VARCHAR(500) NOT NULL                COMMENT '图片链接URL',
    rticle_id  BIGINT       DEFAULT NULL            COMMENT '关联文章ID（可为空）',
    upload_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (image_id),
    KEY idx_article_id (rticle_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';

-- ========================================
-- 4. 评论表
-- ========================================
CREATE TABLE IF NOT EXISTS comment (
    comment_id  BIGINT   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    rticle_id  BIGINT   NOT NULL                COMMENT '文章ID',
    suser_id    BIGINT   NOT NULL                COMMENT '评论作者ID',
    username    VARCHAR(50) NOT NULL             COMMENT '评论作者用户名',
    content     TEXT     NOT NULL                COMMENT '评论内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    like_count  INT      NOT NULL DEFAULT 0      COMMENT '点赞数量',
    PRIMARY KEY (comment_id),
    KEY idx_article_id (rticle_id),
    KEY idx_suser_id (suser_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ========================================
-- 5. 用户点赞关联表
-- ========================================
CREATE TABLE IF NOT EXISTS user_article_like (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT   NOT NULL                COMMENT '点赞用户ID',
    rticle_id  BIGINT   NOT NULL                COMMENT '被点赞文章ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_article (user_id, rticle_id),
    KEY idx_article_id (rticle_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞文章关联表';

-- ========================================
-- 6. 相册照片表
-- ========================================
CREATE TABLE IF NOT EXISTS lbum_photo (
    photo_id     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '照片ID',
    	itle        VARCHAR(128)  DEFAULT NULL             COMMENT '照片标题',
    description  VARCHAR(512)  DEFAULT NULL             COMMENT '照片描述',
    url          VARCHAR(500)  NOT NULL                 COMMENT '图片URL（COS地址）',
    location     VARCHAR(128)  DEFAULT NULL             COMMENT '拍摄地点',
    photo_date   DATE          DEFAULT NULL             COMMENT '拍摄日期',
    	ags         VARCHAR(256)  DEFAULT NULL             COMMENT '标签，逗号分隔',
    lbum_name   VARCHAR(100)  DEFAULT NULL             COMMENT '所属相册名称',
    sort_order   INT           NOT NULL DEFAULT 0       COMMENT '排序权重（越大越靠前）',
    status       TINYINT       NOT NULL DEFAULT 1       COMMENT '状态：0=隐藏, 1=显示',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (photo_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册照片表';

-- ========================================
-- 7. 游记表
-- ========================================
CREATE TABLE IF NOT EXISTS 	ravel_note (
    
ote_id      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '游记ID',
    user_id      BIGINT        NOT NULL DEFAULT 1     COMMENT '作者ID',
    	itle        VARCHAR(200)  NOT NULL                COMMENT '标题',
    summary      VARCHAR(500)  DEFAULT NULL            COMMENT '摘要',
    content      LONGTEXT      DEFAULT NULL            COMMENT '正文（Markdown）',
    cover_url    VARCHAR(500)  DEFAULT NULL            COMMENT '封面图URL',
    images       TEXT          DEFAULT NULL            COMMENT '图片列表（JSON数组）',
    location     VARCHAR(128)  DEFAULT NULL            COMMENT '地点',
    	ravel_date  DATE          DEFAULT NULL            COMMENT '旅行日期',
    	ags         VARCHAR(256)  DEFAULT NULL            COMMENT '标签，逗号分隔',
    ead_time    VARCHAR(20)   DEFAULT NULL            COMMENT '阅读时长',
    iew_count   INT           NOT NULL DEFAULT 0      COMMENT '浏览数',
    status       TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：0=隐藏, 1=显示',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (
ote_id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游记表';

-- ========================================
-- 8. 旅行计划表
-- ========================================
CREATE TABLE IF NOT EXISTS 	ravel_plan (
    plan_id      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    	itle        VARCHAR(200)  NOT NULL                COMMENT '计划标题',
    description  TEXT          DEFAULT NULL            COMMENT '计划描述',
    cover_url    VARCHAR(500)  DEFAULT NULL            COMMENT '封面图URL',
    plan_date    VARCHAR(50)   DEFAULT NULL            COMMENT '计划日期',
    status       VARCHAR(20)   NOT NULL DEFAULT 'planning' COMMENT '状态：planning/ongoing/completed',
    sort_order   INT           NOT NULL DEFAULT 0      COMMENT '排序权重',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (plan_id),
    KEY idx_status (status),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅行计划表';

-- ========================================
-- 查看建表结果
-- ========================================
SHOW TABLES;