# java.net.SocketException:Broken pipi with Quartz
- 分析：mysql空闲关闭连接，导致客户端发起连接失败
- 解决: datasource 增加如下配置
```
	<bean id="quartzDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">

		<property name="driverClassName" value="${quartz.jdbc.driver}"/>
		<property name="url" value="${quartz.jdbc.url}"/>
		<property name="username" value="${quartz.jdbc.username}"/>
		<property name="password" value="${quartz.jdbc.password}"/>
		<!--指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.  -->
		<property name="testWhileIdle" value="true"/>
		<!--验证使用的SQL语句  -->
		<property name="validationQuery" value="SELECT 1"/>
		<!--空闲逐出连接池的时间，单位：毫秒，默认30分钟-->
		<property name="minEvictableIdleTimeMillis" value="1800000"/>
		<!--单位：毫秒，即每隔多少时间去检测一次空闲连接是否超时，默认值为-1，即不开启)-->
		<property name="timeBetweenEvictionRunsMillis" value="1800000"/>
		<!--设定在进行后台对象清理时，每次检查几个链接。默认值是3.
			如果numTestsPerEvictionRun>=0, 则取numTestsPerEvictionRun 和池内的链接数 的较小值 作为每次检测的链接数
			如果numTestsPerEvictionRun<0，则每次检查的链接数是检查时池内链接的总数乘以这个值的负倒数再向上取整的结果。-->
		<property name="numTestsPerEvictionRun" value="3"/>
	</bean>
```


### 参考文档
- 《java-net-socketexception-broken-pipe-with-quartz》 https://stackoverflow.com/questions/9159372/java-net-socketexception-broken-pipe-with-quartz-and-mysql-and-tomcat-tomcat-c