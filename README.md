# 在线教育平台 🎓

全栈 Monorepo — 基于 Spring Boot + Vue3 + Azure Blob Storage

## 项目结构

```
online-education-platform/
├── backend-src/          # Spring Boot 后端（Maven）
├── frontend-portal/      # 学员端（Vue3 + Vite + Element Plus）
├── frontend-admin/      # 管理员端（Vue3 + Vite + Element Plus）
├── frontend-teacher/    # 讲师端（Vue3 + Vite + Element Plus）
├── deploy/             # 部署脚本 & Nginx 配置
└── docs/               # 文档
```

## 快速开始

### 后端

```bash
cd backend-src
mvn clean package -DskipTests
java -jar target/OnlineEdu-0.0.1-SNAPSHOT.jar
```

### 前端（任一）

```bash
cd frontend-portal   # 或 frontend-admin / frontend-teacher
npm install
npm run dev
```

## 核心特性

- ✅ **注册关闭** — 仅管理员可添加账号
- ✅ **Azure Blob Storage** — 视频流使用 Azure Blob，免虚机带宽
- ✅ **三端分离** — Portal / Admin / Teacher 独立前端
- ✅ **JWT 认证** — 无状态登录
- ✅ **RBAC 权限** — ADMIN / TEACHER / STUDENT 角色

## 环境变量

| 变量 | 说明 | 必填 |
|------|------|------|
| `AZURE_STORAGE_CONNECTION_STRING` | Azure Blob 连接字符串 | 视频上传时使用 |
| `DB_PASSWORD` | MySQL 密码 | ✅ |
| `JWT_SECRET` | JWT 签名密钥 | ✅ |
| `QWEN_API_KEY` | 通义千问 API Key | ❌（AI 问答功能） |

## 部署

见 `deploy/` 目录：
- `nginx.conf` — Nginx 反向代理配置
- `online-edu.service` — systemd 服务文件
- `deploy.sh` — 一键部署脚本

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 讲师 | teacher01 | 123456 |
| 学员 | user100 | 123456 |

> ⚠️ 首次登录后请立即修改密码！

## 技术栈

**后端：** Spring Boot 2.7 / MyBatis-Plus / MySQL 8 / Redis / JWT / Knife4j  
**前端：** Vue 3 / Vite / Element Plus / Pinia / ECharts / Axios  
**部署：** Nginx / Azure VM / Azure Blob Storage

## License

MIT
