-- 公网安全加固：现有数据库一次性迁移脚本
-- 本地执行: mysql -u root -p code2026 < security_hardening_migration.sql
-- 生产执行: mysql -u 数据库管理员 -p blog < security_hardening_migration.sql
-- 本脚本故意不包含 USE，避免误操作到另一个环境的数据库。

-- 可重复执行：旧库补 role，新库已经存在时跳过。
SET @has_role = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'role'
);
SET @ddl = IF(@has_role = 0,
  'ALTER TABLE user ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT ''USER'' COMMENT ''角色：ADMIN=管理员, USER=普通用户, DISABLED=禁用'' AFTER password',
  'SELECT 1');
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

-- 历史版本可能保存过明文 Token；先全部失效，再收紧为 SHA-256 哈希长度。
UPDATE user SET token = NULL;
ALTER TABLE user
  MODIFY COLUMN token VARCHAR(64) DEFAULT NULL
  COMMENT '当前有效JWT的SHA-256哈希（不保存明文Token）';

-- 旧管理员先禁用并清除历史会话；密码不写入公开 SQL。
UPDATE user
SET username = 'yeeee',
    name = 'yeeee',
    role = 'DISABLED',
    token = NULL
WHERE user_id = 1;

-- 禁用旧版或已存在的管理员；随后必须使用一次性管理员引导重新设置强密码。
UPDATE user
SET role = 'DISABLED', token = NULL
WHERE name IN ('admin', 'yeeee', 'chip') OR role = 'ADMIN';

-- 执行完本脚本后，按安全部署指南设置 BOOTSTRAP_ADMINS_ENABLED=true
-- 并提供两组全新强密码启动一次。成功后删除密码环境变量并关闭引导开关。
