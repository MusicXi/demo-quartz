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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.cnc.cloud.bean.QrtzJobDetails;
import com.cnc.cloud.dao.QrtzJobDetailsDao;
import com.cnc.cloud.quartz.cluster.job.DynamicQuartzJob;
import com.cnc.cloud.service.QrtzJobDetailsService;


@Service("qrtzJobDetailsService")
@Transactional(rollbackFor=Exception.class)
public class QrtzJobDetailsServiceImpl  implements QrtzJobDetailsService {
	private static final Logger LOGGER=LoggerFactory.getLogger(QrtzJobDetailsServiceImpl.class);
	/** jobName 前缀*/
	private static final String JOB_NAME_PREFIX = "jobName.";
	/** triggerName 前缀*/
	private static final String TRIGGER_NAME_PREFIX = "triggerName.";
	/** 默认组 */
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
		if (qrtzJobDetails == null) {
			throw new Exception("qrtzJobDetails 为空");
		}
		
		if (StringUtils.isBlank(qrtzJobDetails.getDescription())) {
			throw new Exception("qrtzJobDetails serviceInfo 为空");
		}
		String description = qrtzJobDetails.getDescription();
		String jobName = JOB_NAME_PREFIX + description;
		String triggerName = TRIGGER_NAME_PREFIX + description;
		String jobGroup = StringUtils.isBlank(qrtzJobDetails.getJobGroup())? GROUP_DEFAULT : qrtzJobDetails.getJobGroup();
		
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		if (scheduler.checkExists(jobKey)) {
			throw new Exception("jobKey 存在!");
		}
		JobDetail job = JobBuilder.newJob(DynamicQuartzJob.class).withIdentity(jobKey).withDescription(description).build();  			
		TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, jobGroup);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startNow()  
        		.withSchedule(CronScheduleBuilder.cronSchedule("*/10 * * * * ?")).build();
        scheduler.scheduleJob(job, trigger);  
		
//		resultMap.put("success", true);
//		resultMap.put("msg", "创建QrtzJobDetails 成功!");
//		LOGGER.info("创建QrtzJobDetails 成功 " + qrtzJobDetails.toString());
		return resultMap;
	}
	
	@Override
	public Map<String, Object> createQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();

		return resultMap;

	}
	
	@Override
	public Map<String, Object> updateQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();

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






}
