## 青龙面板环境变量更新服务
- 接收来自客户端的cookie
- 对接青龙面板
- cookie更新至面板并持久化至Mysql数据库
接口：http://localhost:5607/saveCookie  
- 请求格式  
  {  
  "userName": "XXXXXXX", // 必须   
  "device": "iPhone 13 Pro",  // 非必须  
  "cookie": "pt_key=xxx;pt_pin=abc123;", // 必须    
  "timestamp": "2025-05-22 14:30:00",  // 非必须     
  }   

对应APP客户端：https://github.com/lun55/auto_jd_cookie