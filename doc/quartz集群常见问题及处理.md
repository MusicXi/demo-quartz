# java.net.SocketException:Broken pipi with Quartz
- 分析：mysql空闲关闭连接，导致客户端发起连接失败
- 解决: datasource 增加如下配置
```
dddd
```