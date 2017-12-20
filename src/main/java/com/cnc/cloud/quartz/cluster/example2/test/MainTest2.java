package com.cnc.cloud.quartz.cluster.example2.test;  
  
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
  
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
		Thread.sleep(30000);
		System.out.println("暂停jobDetail2 任务");
		//sched.pauseJob(JobKey.jobKey("jobName", "groupName"));
		//group 默认default
		sched.pauseJob(JobKey.jobKey("jobDetail2"));
		
		//测试30s后 恢复调度任务
		Thread.sleep(30000);
		System.out.println("过经过30s 恢复jobDetail2 任务");
		sched.resumeJob(JobKey.jobKey("jobDetail2"));
		
		//测试删除
 
    }  
  
}  