# 基于quartz集群分布式动态定时任务管理 

> 真正动态，定时任务“0”开发，基于quartz集群超轻量级扩展实现。

- 不多说，先看效果
![Alt text](https://github.com/MusicXi/demo-quartz/raw/master/doc/images/demo_show.png)

- 添加定时任务
![Alt text](https://github.com/MusicXi/demo-quartz/raw/master/doc/images/task_add.png)

- 编辑定时任务-修改定时规则
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

### 后续版本规划增加功能
1. 自动记录定时任务执行的日志,方便运维
2. 注解形式描述系统所有可用作为定时任务的服务方法,操作界面下拉可选

### 快速启动
1. 在quartz连接的数据库执行sql文件的 分布式定时任务初始化建表.sql的内容。
2. 访问http://localhost:8082/ (端口号自己定义)
3. 接口文档访问地址:http://localhost:8082/swagger-ui.html
4. 添加服务方法及定时周期 测试，启/停/修改定时任务(也可以添加任意自己开发服务，动态生成定时任务)
- helloSerice.sayHello    */5 * * * * ?
- simpleService.testMethod1   */15 * * * * ?
- simpleService.testMethod2   */35 * * * * ?
### 实现原理(画重点)
#### 1. 分析问题:传统定时任务写法有什么问题?
```xml
    <bean id="jobDetail1"
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
	</bean> 
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
```java
package com.cnc.cloud.quartz.cluster.job;  
  
//import 省略...;

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
**QrtzJobDetailsController**
web 管理端接口
```java
package  com.cnc.cloud.controller;

// import 省略...;


//@CrossOrigin(origins={"http://localhost:8080"}, methods={RequestMethod.GET, RequestMethod.POST})
@CrossOrigin(origins={"*"}, methods={RequestMethod.GET, RequestMethod.POST})
@Api(value = "/qrtzJobDetails", tags = "定时任务操作接口")
@Controller
@RequestMapping("/qrtzJobDetails")
public class QrtzJobDetailsController {
	//private static final Logger logger = LoggerFactory.getLogger(QrtzJobDetailsController.class);
	
	@Autowired
	private QrtzJobDetailsService qrtzJobDetailsService;
	

	@ApiOperation(value = "查询定时任务", notes = "根据id获取用户信息", httpMethod = "POST", response = QrtzJobDetails.class)
	@RequestMapping("/listByPage")
	@ResponseBody
	public  Map<String, Object> listByPage(String filter, QrtzJobDetails qrtzJobDetails, Page<Map<String, Object>> page, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		//设置默认排序属性
		//page.setDefaultSort("createTime", "desc");
		page = this.qrtzJobDetailsService.findMapListByPage(qrtzJobDetails, page);
		map.put("studentdata", page);
	    map.put("number", page.getTotal());
		return map;
	}
	
	@ApiOperation(value = "添加定时任务", notes = "动态添加定时任务", httpMethod = "POST", response = QrtzJobDetails.class)
	@RequestMapping("/add")
	@ResponseBody
	public Map<String, Object> addQrtzJobDetails(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();

		map = this.qrtzJobDetailsService.createQrtzJobDetails(qrtzJobDetails);
		map.put("success", true);
		map.put("msg", "定时任务添加成功");
		return map;
	}
	
	@ApiOperation(value = "修改定时任务", notes = "动态修改定时任务", httpMethod = "POST", response = QrtzJobDetails.class)
	@RequestMapping("/edit")
	@ResponseBody
	public Map<String, Object> updateQrtzJobDetails(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.updateQrtzJobDetails(qrtzJobDetails);
		return map;
	}
	
	@ApiOperation(value = "删除定时任务", notes = "动态删除定时任务,先暂停再删除", httpMethod = "POST", response = QrtzJobDetails.class)
	@RequestMapping("/delete")
	@ResponseBody
	public Map<String, Object> deleteQrtzJobDetails(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.deleteQrtzJobDetails(qrtzJobDetails);
		return map;
	}
	
	@ApiOperation(value = "暂停定时任务", notes = "暂停定时任务", httpMethod = "POST", response = QrtzJobDetails.class)
	@RequestMapping("/pause")
	@ResponseBody
	public Map<String, Object> pauseJob(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.pauseJob(qrtzJobDetails);
		return map;
	}
	
	@ApiOperation(value = "恢复定时任务", notes = "恢复暂停的定时任务", httpMethod = "POST", response = QrtzJobDetails.class)
	@RequestMapping("/resume")
	@ResponseBody
	public Map<String, Object> resumeJob(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.resumeJob(qrtzJobDetails);
		return map;
	}
	
}

```

**QrtzJobDetailsServiceImpl** QrtzJobDetailsService接口实现
```java
package com.cnc.cloud.service.impl;

//import 省略...;

@Service("qrtzJobDetailsService")
@Transactional(rollbackFor=Exception.class)
public class QrtzJobDetailsServiceImpl  implements QrtzJobDetailsService {
	private static final Logger LOGGER=LoggerFactory.getLogger(QrtzJobDetailsServiceImpl.class);
	
	/** jobName 前缀*/
	private static final String JOB_NAME_PREFIX = "jobName.";
	/** triggerName 前缀*/
	private static final String TRIGGER_NAME_PREFIX = "triggerName.";
	/** jobName/triggerName 默认组 */
	private static final String GROUP_DEFAULT = "DEFAULT";
	
	@Autowired
	private QrtzJobDetailsDao qrtzJobDetailsDao;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private Scheduler scheduler; 
	
    @Override
	public Map<String, Object> createQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		
		// 非空校验
		if (qrtzJobDetails == null) {
			throw new Exception("qrtzJobDetails 为空");
		}
		if (StringUtils.isBlank(qrtzJobDetails.getJobName())) {
			throw new Exception("qrtzJobDetails serviceInfo 为空");
		}

		// 定时服务有效性校验 (校验是否存在对应的servcie.method )
		this.checkServiceAndMethod(qrtzJobDetails.getJobName());

		// 唯一性校验
		String jobName = JOB_NAME_PREFIX + qrtzJobDetails.getJobName();
		String triggerName = TRIGGER_NAME_PREFIX + qrtzJobDetails.getJobName();
		String jobGroup = StringUtils.isBlank(qrtzJobDetails.getJobGroup())? GROUP_DEFAULT : qrtzJobDetails.getJobGroup();
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		if (scheduler.checkExists(jobKey)) {
			throw new DynamicQuartzException(qrtzJobDetails.getJobName() + "服务方法对应定时任务已经存在!");
		}

		// 构建job信息
		JobDetail job = JobBuilder.newJob(DynamicQuartzJob.class).withIdentity(jobKey).withDescription(qrtzJobDetails.getDescription()).build();  			
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, jobGroup);

        // 构建job的触发规则 cronExpression
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow()  
        		.withSchedule(CronScheduleBuilder.cronSchedule(qrtzJobDetails.getCronExpression())).build();

		// 注册job和trigger信息
        scheduler.scheduleJob(job, trigger);  
		return resultMap;
	}
	
	@Override
	public Map<String, Object> createQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		// TODO 暂未实现批量创建定时任务
		return resultMap;

	}
	
	@Override
	public Map<String, Object> updateQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		JobKey jobKey = JobKey.jobKey(qrtzJobDetails.getJobName(), qrtzJobDetails.getJobGroup());
		TriggerKey triggerKey = null;
		List<? extends Trigger> list = scheduler.getTriggersOfJob(jobKey);
		if (list == null || list.size() != 1) {
			return resultMap;
		}
		for (Trigger trigger : list) {
			//暂停触发器
			scheduler.pauseTrigger(trigger.getKey());
			triggerKey = trigger.getKey();
		}
		Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow()  
              .withSchedule(CronScheduleBuilder.cronSchedule(qrtzJobDetails.getCronExpression())).build();
		scheduler.rescheduleJob(newTrigger.getKey(), newTrigger);
		LOGGER.info("update job name:{} success", qrtzJobDetails.getJobName());
		return resultMap;
	}
	

	@Override
	public Map<String, Object> deleteQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		JobKey jobKey = JobKey.jobKey(qrtzJobDetails.getJobName(), qrtzJobDetails.getJobGroup());
		QuartzUtil.deleteJob(scheduler, jobKey);
		LOGGER.info("delete job name:{} success", qrtzJobDetails.getJobName());
		return resultMap;
	}
	
	@Override
	public Page<Map<String, Object>> findMapListByPage(QrtzJobDetails qrtzJobDetails, Page<Map<String, Object>> page) {
		page = PageHelper.startPage(page.getPageNum(), page.getPageSize());
		this.qrtzJobDetailsDao.selectMapList(qrtzJobDetails);
		return page;
	}
	
	@Override
	public Page<QrtzJobDetails> findListByPage(QrtzJobDetails qrtzJobDetails, Page<QrtzJobDetails> page) {
		page = PageHelper.startPage(page.getPageNum(), page.getPageSize());
		this.qrtzJobDetailsDao.selectList(qrtzJobDetails);
		return page;

	}

	@Override
	public Map<String, Object> pauseJob(QrtzJobDetails qrtzJobDetails)
			throws Exception {
		scheduler.pauseJob(JobKey.jobKey(qrtzJobDetails.getJobName(), qrtzJobDetails.getJobGroup()));
		LOGGER.info("pause job name:{} success", qrtzJobDetails.getJobName());
		return null;
	}

	@Override
	public Map<String, Object> resumeJob(QrtzJobDetails qrtzJobDetails)
			throws Exception {
		scheduler.resumeJob(JobKey.jobKey(qrtzJobDetails.getJobName(), qrtzJobDetails.getJobGroup()));
		LOGGER.info("resume job name:{} success", qrtzJobDetails.getJobName());
		return null;
	}


	/**
	 * <li>校验服务和方法是否存在</li>
	 * @param jobName
	 * @throws DynamicQuartzException
	 */
	private void checkServiceAndMethod(String jobName) throws DynamicQuartzException {
		String[] serviceInfo = jobName.split("\\.");
		String beanName = serviceInfo[0];
		String methodName = serviceInfo[1];
		if (! SpringContextHolder.existBean(beanName)) {
			throw new DynamicQuartzException("找不到对应服务");
		}
		if (! SpringContextHolder.existBeanAndMethod(beanName, methodName, null)) {
			throw new DynamicQuartzException("服务方法不存在");
		}
		
	}
	

}

```
项目地址:https://github.com/MusicXi/demo-quartz.git 

### 交流联系
项目详细设计说明:https://blog.csdn.net/Myron_007/article/details/79856097 博客回复疑问不及时。
如果有问题可以加临时群,439019717,把问题说明截图,贴下下去。解决完可以退出。
### 参考技术资料：
1. Quartz在Spring中集群  http://sundoctor.iteye.com/blog/486055?page=2
2. Spring Boot集成持久化Quartz定时任务管理和界面展示 http://blog.csdn.net/u012907049/article/details/73801122
3. Spring+SpringMVC+mybatis+Quartz整合  http://blog.csdn.net/u012907049/article/details/70273080
4. Vue.js 学习地址:https://cn.vuejs.org/v2/guide/
5. vue-element-admin   https://github.com/PanJiaChen/vue-element-admin/blob/master/README.zh-CN.md
