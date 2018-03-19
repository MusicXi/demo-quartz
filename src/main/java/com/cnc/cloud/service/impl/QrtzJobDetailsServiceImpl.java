package com.cnc.cloud.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cnc.cloud.bean.QrtzJobDetails;
import com.cnc.cloud.dao.QrtzJobDetailsDao;
import com.cnc.cloud.exception.DynamicQuartzException;
import com.cnc.cloud.quartz.cluster.job.DynamicQuartzJob;
import com.cnc.cloud.service.QrtzJobDetailsService;
import com.cnc.cloud.util.QuartzUtil;
import com.cnc.cloud.util.SpringContextHolder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;


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
		checkServiceAndMethod(qrtzJobDetails.getJobName());
		String jobName = JOB_NAME_PREFIX + qrtzJobDetails.getJobName();
		String triggerName = TRIGGER_NAME_PREFIX + qrtzJobDetails.getJobName();
		String jobGroup = StringUtils.isBlank(qrtzJobDetails.getJobGroup())? GROUP_DEFAULT : qrtzJobDetails.getJobGroup();
		
		// 唯一性校验
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		if (scheduler.checkExists(jobKey)) {
			throw new Exception("jobKey 存在!");
		}
		// 构建job信息
		JobDetail job = JobBuilder.newJob(DynamicQuartzJob.class).withIdentity(jobKey).withDescription(qrtzJobDetails.getDescription()).build();  			
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, jobGroup);
        // 构建job的触发规则 cronExpression
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow()  
        		.withSchedule(CronScheduleBuilder.cronSchedule(qrtzJobDetails.getCronExpression())).build();
        scheduler.scheduleJob(job, trigger);  
		
//		resultMap.put("success", true);
//		resultMap.put("msg", "创建QrtzJobDetails 成功!");
//		LOGGER.info("创建QrtzJobDetails 成功 " + qrtzJobDetails.toString());
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
	public Map<String, Object> updateQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();

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
	public Map<String, Object> deleteQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();

		return resultMap;
	}
	
	@Override
	public QrtzJobDetails findQrtzJobDetailsByPrimaryKey(String id) {
		return this.qrtzJobDetailsDao.selectByPrimaryKey(id);
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
	public List<Map<String, Object>> findMapList(QrtzJobDetails qrtzJobDetails) {
		return this.qrtzJobDetailsDao.selectMapList(qrtzJobDetails);
	}
	
	@Override
	public List<QrtzJobDetails> findList(QrtzJobDetails qrtzJobDetails){
		return this.qrtzJobDetailsDao.selectList(qrtzJobDetails);
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
	 * 校验服务和方法是否存在
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
