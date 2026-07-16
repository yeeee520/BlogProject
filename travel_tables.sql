USE code2026;

CREATE TABLE IF NOT EXISTS `travel_note` (
    `note_id`      BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT        NOT NULL DEFAULT 1,
    `title`        VARCHAR(200)  NOT NULL,
    `summary`      VARCHAR(500)  DEFAULT NULL,
    `content`      LONGTEXT      DEFAULT NULL,
    `cover_url`    VARCHAR(500)  DEFAULT NULL,
    `images`       TEXT          DEFAULT NULL,
    `location`     VARCHAR(128)  DEFAULT NULL,
    `travel_date`  DATE          DEFAULT NULL,
    `tags`         VARCHAR(256)  DEFAULT NULL,
    `read_time`    VARCHAR(20)   DEFAULT NULL,
    `view_count`   INT           NOT NULL DEFAULT 0,
    `status`       TINYINT       NOT NULL DEFAULT 1,
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`note_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `travel_plan` (
    `plan_id`      BIGINT        NOT NULL AUTO_INCREMENT,
    `title`        VARCHAR(200)  NOT NULL,
    `description`  TEXT          DEFAULT NULL,
    `cover_url`    VARCHAR(500)  DEFAULT NULL,
    `plan_date`    VARCHAR(50)   DEFAULT NULL,
    `status`       VARCHAR(20)   NOT NULL DEFAULT 'planning',
    `sort_order`   INT           NOT NULL DEFAULT 0,
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`plan_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

