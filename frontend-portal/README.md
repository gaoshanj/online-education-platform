# 在线教育平台用户端

## 1. 项目简介  
本项目为《基于SpringBoot + Vue3的在线教育平台》的**用户端**（前台展示系统）前端代码。系统采用 Vue 3 + Vite + Element Plus 技术栈构建，为平台学生及普通用户提供课程浏览、在线学习、个人资料管理及购买课程等核心前台管控功能。本前端项目与独立开发的服务端 API 进行数据交互，实现了完全的前后端分离。

- **后端**  
  Gitee地址：[https://gitee.com/feng_s/online-education](https://gitee.com/feng_s/online-education)

- **讲师端（讲师管理系统）**  
  Gitee地址：[https://gitee.com/feng_s/online-education-teacher](https://gitee.com/feng_s/online-education-teacher)

- **管理员端（后台管理系统）**  
  Gitee地址：[https://gitee.com/feng_s/online-education-admin](https://gitee.com/feng_s/online-education-admin)

## 2. 运行环境要求  
以下为前端服务成功编译与运行的必要环境及版本要求：

- **操作系统**：Windows / Linux / macOS
- **Node.js版本**：**Node.js 18.x** 及以上（LTS版本推荐），这是运行和构建 Vue3/Vite 项目的必需环境。
- **包管理工具**：npm（随 Node.js 安装自带）或 yarn / pnpm。
- **推荐浏览器**：Chrome / Edge（最新正式版），以保证最佳的渲染效果和兼容性。
- **开发工具**：推荐使用 Visual Studio Code (VS Code)、WebStorm 或相关前端 IDE，并安装 Vue/Volar 插件。

## 3. 安装项目依赖  
使用命令行终端（Terminal或CMD）进入项目的根目录（即 `package.json` 所在的目录），执行以下命令下载所需的第三方依赖包：
```bash
npm install
```
*提示：由于网络原因，国内用户如果执行过慢或报错，建议配置淘宝镜像源，使用 `npm config set registry https://registry.npmmirror.com` 命令后再执行安装。*

## 4. 修改前端配置 (对接后端)  
在启动前端项目前，你需要确保本地网络请求能够正确路由到你的后端服务。本项目借助 Vite 的代理特性（Proxy）解决开发环境的跨域问题。

**配置文件路径**：根目录下的 `vite.config.js`

打卡该文件，确认 `server.proxy` 中的 `target` 是否与你后端实际运行的地址和端口保持一致。如果你的后端按照默认配置运行于本机的 `8088` 端口，则通常无需修改代码。示例如下：

```javascript
export default defineConfig({
  // ... 其他配置
  server: {
    port: 5173,          // 前端开发服务器启动端口
    proxy: {
      '/api': { 
        target: 'http://localhost:8088', // 此处配置为后端服务的实际请求地址
        changeOrigin: true
      }
    }
  }
})
```
> **注意**：进行这一步测试前，请确保《在线教育平台后端服务》已经在本地成功启动运行。如果不对应或后端未启动，前端登录及获取数据时将会报错。

## 5. 项目启动步骤  

该项目为标准的基于 Vite 构建的现代前端工程。

1. 在项目根目录的命令行中输入以下命令以启动本地开发服务器：
   ```bash
   npm run dev
   ```

2. 出现类似如下信息时，表示前端开发服务已成功启动：
   ```text
     VITE vX.X.X  ready in XXX ms

     ➜  Local:   http://localhost:5173/
     ➜  Network: use --host to expose
     ➜  press h to show help
   ```

## 6. 访问与测试  

在浏览器地址栏中输入上一步控制台中打印的 Local 地址进行访问：

**用户端访问地址**：  
[http://localhost:5173](http://localhost:5173)

**验证方法**：  
若页面成功渲染出前台系统的首页，输入下方的默认测试用户账号进行登录测试。登录成功后能够跳转至个人中心并正常浏览课程数据，即代表前端已成功启动并完美对接后端服务与数据库系统。

## 7. 默认测试账号

系统在数据库初始化时，提供了一个普通用户账号供测试使用：

- **普通用户/学生账号**：`student01` 
- **账号密码**：`123456`

## 8. 常见问题说明  

**1. 依赖安装失败 (npm install 报错)**
- **现象特征**：控制台大片标红抛出 `ERR!` 提示，最终未能生成 `node_modules` 文件夹。
- **排查解决**：多为国内网络原因导致的仓库连接超时。请尝试切换网络或手动设置为国内淘宝 npm 镜像源后再重新执行。

**2. 页面能打开，但点击登录/获取数据一直转圈或者右上角报错**
- **现象特征**：控制台/网络抓包中显示接口为 `500 Internal Server Error` 或是 `Network Error` 且一直没返回。
- **排查解决**：
  - 首要必须检查**后端 Spring Boot 应用程序是否处于正在运行**的状态。
  - 检查后端服务控制台是否有最新的异常报错打印。
  - 核查 `vite.config.js` 中的代理 `target` 是否填写成了错误的后端地址。

**3. 端口被占用**
- **现象特征**：控制台打印红字提示 `Error: listen EADDRINUSE: address already in use :::5173`。
- **排查解决**：意味着你电脑的 5173 端口正在被别的程序占用使用。可通过在 `vite.config.js` 中的 `server: { port: 5174 }` 修改为一个未被占用的新端口号即可重新启动。
