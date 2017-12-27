# quartz集群 

### 说明内容
https://github.com/MusicXi/demo-quartz.git

#### 1.quartz 集群如何工作
> 一个 Quartz 集群中的每个节点是一个独立的 Quartz 应用，它又管理着其他的节点。也就是你必须对每个节点分别启动或停止。
Quartz 应用是通过数据库表来感知到另一应用的

#### 2.quartz集群类型
水平集群：
垂直集群：
```
var e = "ssss";
```


### Quartz集群特点

#### 1.优点：
 Quartz的集群功能保证了任务可靠、高效的正常执行，当集群中其中的一个节点出问题时，另外的节点接手任务，继续工作。确保所有的job的到执行
 
#### 2.缺陷及解决：
1、时间规则更改不方便，需同步更改数据库时间规则描述
2、Quartz集群当所有节点跑在同一台服务器上，当服务器崩溃时所有节点将终止，定时任务将不能正常执行
3、Quartz集群当节点不在同一台服务器上时，因时钟的可能不同步导致节点对其他节点状态的产生影响。



#### 3.问题：
1.修改定时任务配置,怎么生效?
2.怎么标识是那台机器的实例?

### 集群环境下开发注意
1.修改Job的类名称及包路径时 QuartzJobBean,需要修改qrtz_job_details.JOB_CLASS_NAME 对应名称. 或者初始化qrtz库表。
否则将报如下错误:
``` 
 org.quartz.JobPersistenceException: Couldn't store trigger 'DEFAULT.trigger1' for 'DEFAULT.jobDetail1' job:com.sundoctor.quartz.cluster.example.MyQuartzJobBean1 [See nested exception: java.lang.ClassNotFoundException: com.sundoctor.quartz.cluster.example.MyQuartzJobBean1]

```
### 参考技术资料：
1. Quartz在Spring中集群  http://sundoctor.iteye.com/blog/486055?page=2
2. Spring Boot集成持久化Quartz定时任务管理和界面展示 http://blog.csdn.net/u012907049/article/details/73801122
3. Spring+SpringMVC+mybatis+Quartz整合  http://blog.csdn.net/u012907049/article/details/70273080
