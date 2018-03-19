package com.cnc.cloud.quartz.cluster.job;  
  
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;


/**
 * 动态定时任务Job
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