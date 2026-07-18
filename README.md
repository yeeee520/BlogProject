# BlogProject

一个前后端分离的个人博客项目，包含文章、游记、旅行计划、公开/私密相册和后台管理功能。项目已经完成公网部署前的基础安全加固，并提供 Nginx、systemd、数据库迁移与腾讯云 COS 配置模板。

## 主要功能

- 文章、游记和旅行计划的展示与管理
- 相册公开/私密分级、批量调整可见性
- 私密照片仅管理员可见，通过短时签名 URL 访问
- 腾讯云 COS 图片上传、下载、删除与对象 ACL 管理
- 双管理员账号的一次性安全初始化
- JWT 登录、角色权限控制和登录防爆破
- 富文本展示清洗、图片文件头校验和精确 CORS 白名单

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite、Pinia、Vue Router、Element Plus、Axios |
| 后端 | Java 21、Spring Boot 3.4、Spring Security、MyBatis、JWT |
| 数据库 | MySQL 8 |
| 对象存储 | 腾讯云 COS |
| 部署 | Nginx、systemd、腾讯云免费 DV SSL 证书 |

## 项目结构

```text
BlogProject/
├─ vue/                              # Vue 前端
├─ springboot/                       # Spring Boot 后端
├─ deploy/                           # Nginx、systemd、环境变量模板
├─ init.sql                          # 最终数据库结构
├─ album_visibility_migration.sql    # 相册可见性迁移
├─ security_hardening_migration.sql  # 安全加固迁移
├─ 安全部署指南.md
└─ 服务器部署指南.md
```

## 环境要求

- JDK 21
- Maven 3.9+
- Node.js `^20.19.0` 或 `>=22.12.0`
- MySQL 8
- 腾讯云 COS Bucket（需要图片功能时）

## 本地启动

### 1. 初始化数据库

本地数据库名为 `code2026`。先创建数据库，再从项目根目录导入最终结构：

```sql
CREATE DATABASE code2026 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -u root -p code2026 < init.sql
```

`init.sql` 不包含 `USE`，必须在命令中显式指定目标数据库，避免误操作其他环境。

### 2. 创建本地配置

在项目根目录执行：

```powershell
Copy-Item springboot/src/main/resources/application-local.example.yaml springboot/application-local.yaml
```

然后编辑 `springboot/application-local.yaml`，填写本地数据库密码、随机 JWT 密钥和 COS 配置。该文件已被 Git 忽略，不要移动到 `src/main/resources`，也不要提交任何真实密码、域名、IP、Bucket 名称或云密钥。

JWT 密钥应使用至少 32 字节的随机值，例如：

```bash
openssl rand -base64 64
```

### 3. 一次性初始化管理员

全新数据库不包含管理员密码。首次启动前，通过本地环境变量临时提供两组不同且至少 14 个字符的强密码：

```powershell
$env:BOOTSTRAP_ADMINS_ENABLED='true'
$env:BOOTSTRAP_YEEEE_PASSWORD='请替换为第一组全新强密码'
$env:BOOTSTRAP_CHIP_PASSWORD='请替换为另一组不同的全新强密码'
```

启动并确认日志出现“管理员一次性引导完成”后，立即删除密码变量并关闭引导开关，再重启后端：

```powershell
Remove-Item Env:BOOTSTRAP_YEEEE_PASSWORD
Remove-Item Env:BOOTSTRAP_CHIP_PASSWORD
$env:BOOTSTRAP_ADMINS_ENABLED='false'
```

不要使用曾经出现在聊天、截图、Git 历史或旧配置中的密码。

### 4. 启动后端

```powershell
Set-Location springboot
mvn spring-boot:run
```

后端默认监听 `127.0.0.1:8080`。

### 5. 启动前端

另开一个终端：

```powershell
Set-Location vue
npm ci
npm run dev
```

开发地址默认为 `http://localhost:5173`。

## 数据库环境

| 环境 | 数据库名 | 用途 |
| --- | --- | --- |
| 本地开发 | `code2026` | 本地运行与测试 |
| 生产服务器 | `blog` | 公网生产数据 |

- 全新数据库：直接导入 `init.sql`。
- 已有数据库：备份后按顺序执行 `album_visibility_migration.sql` 和 `security_hardening_migration.sql`。
- 所有脚本都应在命令中显式选择数据库；生产迁移必须明确指定 `blog`。
- 数据库运行账号只授予业务所需的 `SELECT`、`INSERT`、`UPDATE`、`DELETE` 权限，结构迁移使用单独账号。

## 测试与构建

后端：

```powershell
Set-Location springboot
mvn clean test package
```

前端：

```powershell
Set-Location vue
npm ci
npm run test:unit
npm run build
npm audit --omit=dev
```

## 生产部署

生产配置示例位于 `deploy/blog-backend.env.example`。复制到服务器的 `/etc/blog-backend.env` 后填写真实值，并将权限设置为 `600`。真实配置不得保存在仓库中。

推荐顺序：

1. 备份 `blog` 数据库和 COS 对象。
2. 拉取代码并完成数据库迁移。
3. 构建后端 JAR 和前端静态文件。
4. 安装 `deploy/systemd/blog-backend.service`，后端仅绑定 `127.0.0.1:8080`。
5. 域名备案和 DNS 生效后，申请腾讯云免费 DV SSL 证书。
6. 配置 `deploy/nginx/blog.conf.template`，只对公网开放 80、443；不要开放 8080 和 3306。
7. 完成一次性管理员引导、私密 COS ACL 同步和安全验收。

完整操作以 [安全部署指南](安全部署指南.md) 和 [服务器部署指南](服务器部署指南.md) 为准。

## 安全说明

- 公共接口只返回公开且启用的内容，写操作要求管理员身份。
- 项目不提供公网注册入口，管理员密码使用 BCrypt 保存。
- JWT 明文不会写入数据库；同一账号重新登录后旧 Token 失效。
- 连续登录失败会触发 IP 级限流。
- 私密 COS 对象使用私有 ACL 和短时签名 URL。
- CORS 只允许明确配置的来源，前端渲染内容经过 DOMPurify 清洗。
- 上传图片同时校验扩展名、MIME 类型和文件头。
- 公网部署必须启用 HTTPS；HTTPS 可用前不要通过公网 HTTP 输入管理员密码。

仓库是公开的。提交前请检查暂存区，严禁提交 `.env`、`application-local.yaml`、证书私钥、数据库备份、访问令牌或任何真实凭据。如果凭据曾经进入 Git 历史，应立即在对应服务中轮换，单纯删除当前文件并不能使旧凭据失效。

## 相关文档

- [相册公开/私密分级需求](需求文档-相册公开私密分级.md)
- [个人博客后端开发规格说明书](个人博客后端开发规格说明书.md)
- [安全部署指南](安全部署指南.md)
- [服务器部署指南](服务器部署指南.md)
- [云服务器运维手册](云服务器运维手册.md)
