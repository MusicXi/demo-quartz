package com.cnc.cloud.quartz.cluster.example;  
  
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
import org.springframework.util.StringUtils;


  
@PersistJobDataAfterExecution  
@DisallowConcurrentExecution// 不允许并发执行  
public class MyQuartzJobBean1 extends QuartzJobBean {  
  
    private static final Logger logger = LoggerFactory.getLogger(MyQuartzJobBean1.class);  
  
    @Override  
    protected void executeInternal(JobExecutionContext jobexecutioncontext) throws JobExecutionException {  
  
		String instanceId = jobexecutioncontext.getFireInstanceId();
		JobDetail j = jobexecutioncontext.getJobDetail();
		String description = j.getDescription();
		if (StringUtils.isEmpty(description)) {
			return;
		}
		String[] str = description.split("\\.");
		String beanName = str[0];
		String methodName = str[1];
		Object simpleService = getApplicationContext(jobexecutioncontext)
				.getBean(beanName);
		Method method;
		try {
			Class<?>[] parameterTypes = null;
			Object[] arguments = null;
			method = simpleService.getClass().getMethod(methodName,
					parameterTypes);
			System.out.println(instanceId + "(" + description + ") =>> "+ simpleService.getClass().getSimpleName() + "." + methodName);
			method.invoke(simpleService, arguments);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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