package com.cnc.cloud.util;

import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * quartz 操作工具类
 * @author linrx1
 *
 */
public class QuartzUtil {
	
	/**
	 * 删除job
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
