# java.net.SocketException:Broken pipi with Quartz
- 分析：mysql空闲关闭连接，导致客户端发起连接失败
- 解决: datasource 增加如下配置
```
dddd
```


### 参考文档
- 《java-net-socketexception-broken-pipe-with-quartz》 https://stackoverflow.com/questions/9159372/java-net-socketexception-broken-pipe-with-quartz-and-mysql-and-tomcat-tomcat-c