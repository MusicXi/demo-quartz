package com.cnc.cloud.quartz.cluster.example;  
  
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cnc.cloud.service.SimpleService;


  
@PersistJobDataAfterExecution  
@DisallowConcurrentExecution// 不允许并发执行  
public class MyQuartzJobBean1 extends QuartzJobBean {  
  
    private static final Logger logger = LoggerFactory.getLogger(MyQuartzJobBean1.class);  
  
    @Override  
    protected void executeInternal(JobExecutionContext jobexecutioncontext) throws JobExecutionException {  
  
        SimpleService simpleService = getApplicationContext(jobexecutioncontext).getBean("simpleService",  
                SimpleService.class);  
        String instanceId = jobexecutioncontext.getFireInstanceId();
        JobDetail j=jobexecutioncontext.getJobDetail();
        String description = j.getDescription();
        System.out.println(instanceId+ "("+ description + ") =>> "  + simpleService.testMethod1());

  
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