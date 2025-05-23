## 青龙面板环境变量更新服务
- 接收来自客户端的cookie
- 对接青龙面板
- cookie更新至面板并持久化至Mysql数据库
# 🚀 Docker 运行配置指南

将客户端发送的 cookie 更新到青龙面板  
👉 GitHub 项目地址：[https://github.com/lun55/jd_cookie_server](https://github.com/lun55/jd_cookie_server)  
👉 手机APP cookie采集客户端项目地址：[https://github.com/lun55/auto_jd_cookie](https://github.com/lun55/auto_jd_cookie)
---

## 🧱 容器运行命令

```bash
docker run -d \
  --name jd-server \
  -p 5607:5607 \
  -v /root/docker/jd_server/application.yml:/app/config/application.yml \
  menghen/jd-server
```

### 参数说明

| 参数         | 说明                                           |
|--------------|------------------------------------------------|
| `-d`         | 后台运行容器                                   |
| `--name`     | 指定容器名称                                   |
| `-p`         | 端口映射（格式：主机端口:容器端口）            |
| `-v`         | 配置文件挂载（格式：主机路径:容器路径）        |

---

## 📄 配置文件示例（`application.yml`）

```yaml
server:
  port: 5607
  tomcat:
    uri-encoding: UTF-8

spring:
  application:
    name: jd-service

  datasource:
    url: jdbc:mysql://mysql_host:3306/jd_cookie?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
    username: db_username
    password: db_password
    driver-class-name: com.mysql.cj.jdbc.Driver

ql:
  address: http://ql_host:5600
  old-version: false
  username: ql_username
  password: ql_password
  token: ql_token
```

---

## ⚙️ 配置参数说明

### 🔧 服务器配置

| 参数                             | 类型     | 默认值 | 说明           |
|----------------------------------|----------|--------|----------------|
| `server.port`                    | int      | 5607   | 服务监听端口   |
| `server.tomcat.uri-encoding`     | String   | UTF-8  | URI 编码格式   |

---

### 🗃️ 数据源配置

| 参数                                    | 说明             |
|-----------------------------------------|------------------|
| `spring.datasource.url`                 | MySQL 连接地址   |
| `spring.datasource.username`            | 数据库用户名     |
| `spring.datasource.password`            | 数据库密码       |
| `spring.datasource.driver-class-name`   | JDBC 驱动类名    |

---

### 🐉 青龙配置

| 参数             | 说明                              |
|------------------|-----------------------------------|
| `ql.address`     | 青龙面板地址                      |
| `ql.old-version` | 是否旧版本（一般不用填写）        |
| `ql.username`    | 青龙 `client_id`                  |
| `ql.password`    | 青龙 `client_secret`              |
| `ql.token`       | API 访问令牌（一般不用填写）      |

---
---

## 📤 发送数据格式

请按照以下 JSON 格式向服务端发送数据：  
接口：http://localhost:5607/saveCookie  
```json
{
  "userName": "XXXXXXX",               // 必须
  "device": "iPhone 13 Pro",           // 非必须
  "cookie": "pt_key=xxx;pt_pin=abc123;", // 必须
  "timestamp": "2025-05-22 14:30:00"   // 非必须
}
```

说明：
- `userName`：用户名。
- `device`：发送请求的设备信息，可选字段。
- `cookie`：格式为 `pt_key=xxx;pt_pin=xxx;`，必须字段，注意分号 `;` 不可省略。
- `timestamp`：请求发送的时间戳，格式为 `yyyy-MM-dd HH:mm:ss`，可选字段。
## 🗄️ Mysql数据库初始化脚本
创建数据库`jd_cookie`
```sql
CREATE DATABASE IF NOT EXISTS jd_cookie DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
创建表 `user_cookies`
```sql
CREATE TABLE `user_cookies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '用户名',
  `device` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cookie` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```


