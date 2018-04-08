# 基于quartz集群分布式动态定时任务管理 

> https://github.com/MusicXi/demo-quartz.git 指定任一服务方法创建定时任务，“0”开发

- 不多说，先看效果
![Alt text](https://github.com/MusicXi/demo-quartz/raw/master/doc/images/demo_show.png)

- 添加定时任务
![Alt text](https://github.com/MusicXi/demo-quartz/raw/master/doc/images/task_add.png)

- 编辑定时任务-需改时间
![Alt text](https://github.com/MusicXi/demo-quartz/raw/master/doc/images/task_edit.png)

### 特点：
1. 基于quartz集群,轻量扩展。可快速改造现有项目
2. 完全动态管理,可自定义任何XxxService.xxx()服务方法为定时任务,简化定时任务开发配置过程

### 实现功能：
1. 整体性:定时任务集群,同一任务同一时间只有一个实例在执行
2. 可用性:集群可用情况下,单个实例的存活,不影响整体定时任务的执行(quartz集群本身功能)
3. 灵活性:任意节点，访问管理界面，动态管理定时任务(动态创建修改删除定时任务)

### 技术选型
- quartz.version 2.2.1
- spring.version 4.2.6.RELEASE
- mybatis.version 3.3.0
- pagehelper.version 5.0.0

### 当前demo版本 1.1.0 添加功能:
1. 定时任务添加服务校验,必须存在服务及对应方法
2. 事务支持 不符合校验无法添加任务

### 快速启动
1. 在quartz连接的数据库执行sql文件的 分布式定时任务初始化建表.sql的内容。
2. 访问http://localhost:8082/ (端口号自己定义)
3. 添加服务方法及定时周期 测试，启/停/修改定时任务(也可以添加任意自己开发服务，动态生成定时任务)
- helloSerice.sayHello    */5 * * * * ?
- simpleService.testMethod1   */15 * * * * ?
- simpleService.testMethod2   */35 * * * * ?

### 实现原理(画重点)
#### 1. 分析问题:传统定时任务写法有什么问题?
```
<!-- <bean id="jobDetail1"
		class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
		<property name="jobClass">
			<value>com.cnc.cloud.quartz.cluster.example.MyQuartzJobBean1</value>
		</property>
		<property name="durability" value="true" />
		<property name="requestsRecovery" value="true" />
	</bean>
	<bean id="trigger1"
		class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
		<property name="jobDetail" ref="jobDetail1" />
		<property name="cronExpression" value="*/5 * * * * ?" />
	</bean>

	<bean id="jobDetail2"
		class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
		<property name="jobClass">
			<value>com.cnc.cloud.quartz.cluster.example.MyQuartzJobBean2</value>
		</property>
		<property name="durability" value="true" />
		<property name="requestsRecovery" value="true" />
	</bean>
	<bean id="trigger2"
		class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
		<property name="jobDetail" ref="jobDetail2" />
		<property name="cronExpression" value="*/10 * * * * ?" />
	</bean> -->
```
要实现一个定时任务，一般要定义一个job来包装一个定时任务内容，同时定义至少一个trigger来描述该任务触发规则。而job中执行内容一般
是某个业务bean的方法(比如service服务中的方法)。这个写法没问题,但是还是有很多不足的地方:

1. 既然具体区分定时任务执行内容本质上是xxxJob中xxxBean.method。那么为每个任务包装一个xxxJob是否太多余，开发太麻烦？
2. job的名称或者Id缺乏意义化("jobDetail2"简直反人类)，很难通过配置就看出"MyQuartzJobBean"里面执行的业务含义？基于开发人员编码习惯是否良好，如何强制规范？
3. 如何在线上环境修改静态定时任务配置，让产品人员自己去定时任务的起停及执行周期? 少麻烦开发?

下面从三个方面解决
#### 2. 解决问题三板斧
1. 抽象化 :既然决定定时任务内容不是job本身决定，所有定时任务的job都抽象化为DynamicQuartzJob(动态定时任务类)
2. 规则化 :任务名称都使用使用"jobName.xxxService.sssMethod" 来描,xxxService.sssMethod是否可用必须通过系统校验，才能添加任务 
3. 界面化 :任务创建,修改，删除，暂停，回复通过web管理端来控制。产品人员可自行配制

#### 3. 核心代码说明
**DynamicQuartzJob**
```
package com.cnc.cloud.quartz.cluster.job;  
  
//import ...省略;

/**
 * 动态定时任务Job
 * 通过jobName格式化解析，获取到job执行的信息,通过spring上下文获取目标服务执行方法。从而实现动态执行目的
 * @author linrx1
 *
 */
@PersistJobDataAfterExecution  
@DisallowConcurrentExecution// 不允许并发执行  
public class DynamicQuartzJob extends QuartzJobBean {  
  
    private static final Logger logger = LoggerFactory.getLogger(DynamicQuartzJob.class);  
  
    @Override  
    protected void executeInternal(JobExecutionContext jobexecutioncontext) throws JobExecutionException {  
    	// use JobDetailImpl replace JobDetail for get jobName
    	JobDetailImpl jobDetail = (JobDetailImpl) jobexecutioncontext.getJobDetail();
		String name = jobDetail.getName();
		if (StringUtils.isEmpty(name)) {
			throw new JobExecutionException("can not find service info, because desription is empty");
		}
		String[] serviceInfo = name.split("\\.");
		// serviceInfo[0] is JOB_NAME_PREFIX 
		String beanName = serviceInfo[1];
		String methodName = serviceInfo[2];
		Object serviceImpl = getApplicationContext(jobexecutioncontext).getBean(beanName);
		Method method;
		try {
			Class<?>[] parameterTypes = null;
			Object[] arguments = null;
			method = serviceImpl.getClass().getMethod(methodName,parameterTypes);
			logger.info("dynamic invoke {}.{}()", serviceImpl.getClass().getName(), methodName);
			method.invoke(serviceImpl, arguments);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.error("reflect invoke service method error", e);
		}

  
    }  
  
    private ApplicationContext getApplicationContext(final JobExecutionContext jobexecutioncontext) {  
        try {  
            return (ApplicationContext) jobexecutioncontext.getScheduler().getContext().get("applicationContextKey");  
        } catch (SchedulerException e) {  
            logger.error("jobexecutioncontext.getScheduler().getContext() error!", e);  
            throw new RuntimeException(e);  
        }  
    }  
  
}  
```

### 1.quartz 集群如何工作
> 一个 Quartz 集群中的每个节点是一个独立的 Quartz 应用，它又管理着其他的节点。也就是你必须对每个节点分别启动或停止。
Quartz 应用是通过数据库表来感知到另一应用的

### 2.quartz集群类型
- 水平集群：当集群是放置在不同的机器上时，我们称之为水平集群。
- 垂直集群：如果所有节点是跑在同一台机器的时候，我们称之为垂直集群。


## Quartz集群特点

### 1.优点：
 Quartz的集群功能保证了任务可靠、高效的正常执行，当集群中其中的一个节点出问题时，另外的节点接手任务，继续工作。确保所有的job的到执行
 
### 2.缺陷及解决：
1、时间规则更改不方便，需同步更改数据库时间规则描述
2、Quartz集群当所有节点跑在同一台服务器上，当服务器崩溃时所有节点将终止，定时任务将不能正常执行
3、Quartz集群当节点不在同一台服务器上时，因时钟的可能不同步导致节点对其他节点状态的产生影响。



### 3.问题：
1. 修改定时任务配置,怎么生效?
- 使用quartz相关api
2. 怎么标识是那台机器的实例?
- qrtz_scheduler_state 保存服务器实例标识

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
4. Vue.js 学习地址:https://cn.vuejs.org/v2/guide/
5. vue-element-admin   https://github.com/PanJiaChen/vue-element-admin/blob/master/README.zh-CN.md
