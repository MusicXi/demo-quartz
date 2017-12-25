package com.cnc.cloud.quartz.cluster.test;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.cnc.cloud.quartz.cluster.job.DynamicQuartzJob;




public class BuisnessQuartzJobTest {

	public static void main(String[] args) throws SchedulerException {
	    ApplicationContext springContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring/spring-context.xml","classpath:spring/spring-context-quartz.xml"});  
		// 通过SchedulerFactory获取一个调度器实例
		SchedulerFactoryBean factory = springContext.getBean(SchedulerFactoryBean.class);
		Scheduler sched = factory.getScheduler();
		
		
		JobKey jobKey = JobKey.jobKey("jobName.helloService.hello", "DEFAULT");
		TriggerKey triggerKey = TriggerKey.triggerKey("triggerName.helloService.hello", "DEFAULT");
		sched.deleteJob(jobKey);
		JobDetail job = sched.getJobDetail(jobKey);
		if (job == null) {
			job = JobBuilder.newJob(DynamicQuartzJob.class).withIdentity(jobKey).withDescription("helloService.sayHello").build();  			
		}
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow()  
        		.withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?")).build();
        sched.scheduleJob(job, trigger);  
 

	}
	
	public static void deleteJob(Scheduler sched, JobKey jobkey) throws SchedulerException {
		if (! sched.checkExists(jobkey)) {
			return;
		}
		List<? extends Trigger> list = sched.getTriggersOfJob(jobkey);
		for (Trigger trigger : list) {
			//暂停触发器
			sched.pauseTrigger(trigger.getKey());
			//移除触发器
			sched.unscheduleJob(trigger.getKey());			
		}
		sched.deleteJob(jobkey);

	}

}
