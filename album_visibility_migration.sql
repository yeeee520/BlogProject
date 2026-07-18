-- 相册公开/私密分级：现有数据库一次性迁移脚本
-- 本地执行: mysql -u root -p code2026 < album_visibility_migration.sql
-- 生产执行: mysql -u 数据库管理员 -p blog < album_visibility_migration.sql
-- 本脚本故意不包含 USE，避免误操作到另一个环境的数据库。

-- 可重复执行：已有照片默认保持私密，避免迁移后意外公开。
SET @has_is_public = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'album_photo' AND COLUMN_NAME = 'is_public'
);
SET @ddl = IF(@has_is_public = 0,
  'ALTER TABLE album_photo ADD COLUMN is_public TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''是否公开：0=私密, 1=公开'' AFTER status',
  'SELECT 1');
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @has_public_index = (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'album_photo' AND INDEX_NAME = 'idx_is_public'
);
SET @ddl = IF(@has_public_index = 0,
  'ALTER TABLE album_photo ADD KEY idx_is_public (is_public)',
  'SELECT 1');
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;

SET @has_visibility_index = (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'album_photo' AND INDEX_NAME = 'idx_visibility'
);
SET @ddl = IF(@has_visibility_index = 0,
  'ALTER TABLE album_photo ADD KEY idx_visibility (status, is_public)',
  'SELECT 1');
PREPARE migration_stmt FROM @ddl;
EXECUTE migration_stmt;
DEALLOCATE PREPARE migration_stmt;
