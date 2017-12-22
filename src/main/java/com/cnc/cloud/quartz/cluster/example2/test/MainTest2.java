package com.cnc.cloud.quartz.cluster.example2.test;  
  
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.cnc.cloud.quartz.cluster.example.MyQuartzJobBean2;
  
public class MainTest2 {  
  
    /** 
     * @param args 
     * @throws SchedulerException 
     * @throws InterruptedException 
     */  
    public static void main(String[] args) throws SchedulerException, InterruptedException {  
        ApplicationContext springContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spring/spring-context.xml","classpath:spring/spring-context-quartz.xml"});  
		// 通过SchedulerFactory获取一个调度器实例
		SchedulerFactoryBean factory = springContext.getBean(SchedulerFactoryBean.class);
		Scheduler sched = factory.getScheduler();

		sched.resumeJob(JobKey.jobKey("jobDetail1"));
		sched.resumeJob(JobKey.jobKey("jobDetail2"));

		//测试30s后 暂停调度任务
		//Thread.sleep(30000);
		System.out.println("暂停jobDetail2 任务");
		//sched.pauseJob(JobKey.jobKey("jobName", "groupName"));
		//group 默认default
		sched.pauseJob(JobKey.jobKey("jobDetail2"));
		
		//测试30s后 恢复调度任务
		//Thread.sleep(30000);
		System.out.println("过经过30s 恢复jobDetail2 任务");
		sched.resumeJob(JobKey.jobKey("jobDetail2"));
		
		//测试删除-步骤
		//1.暂停触发器
		//2.停止调度任务
		//3.删除调度任务(qrtz_job_detail)
		System.out.println("删除jobDetail2 任务");
      /*  sched.pauseTrigger(TriggerKey.triggerKey("jobDetail2", "DEFAULT"));
        sched.unscheduleJob(TriggerKey.triggerKey("jobDetail2", "DEFAULT"));*/
        sched.deleteJob(JobKey.jobKey("jobDetail2", "DEFAULT"));
        
        
        //测试启动一个调度器 
        sched.deleteJob(JobKey.jobKey("jobDetail2_1_1"));
        deleteJob(sched, JobKey.jobKey("jobDetail2_1"));
        JobDetail job = JobBuilder.newJob(MyQuartzJobBean2.class).withIdentity("jobDetail2_1", "DEFAULT").build();  
//        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger2_1", "DEFAULT").startNow()  
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(15).repeatForever()).build();  
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger2_1", "DEFAULT").startNow()  
                .withSchedule(CronScheduleBuilder.cronSchedule("*/1 * * * * ?")).build();
        sched.scheduleJob(job, trigger);  
        
        Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity("trigger2_1", "DEFAULT").startNow()  
                .withSchedule(CronScheduleBuilder.cronSchedule("*/100 * * * * ?")).build();
        sched.rescheduleJob(TriggerKey.triggerKey("trigger2_1", "DEFAULT"), newTrigger);


    }  
    
    /**
     * 删除一个job任务
     * @param sched
     * @param jobkey
     * @throws SchedulerException
     */
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