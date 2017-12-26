package com.cnc.cloud.quartz.cluster.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cnc.cloud.bean.QrtzJobDetails;
import com.cnc.cloud.dao.QrtzJobDetailsDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/spring-context.xml"})
public class QuartzDBTest {
	
	@Autowired
	private QrtzJobDetailsDao qrtzJobDetailsDao;
	
	@Test
	public void listJobDetail() {
		List<QrtzJobDetails> list = new ArrayList<>();
		QrtzJobDetails qrtzJobDetails = new QrtzJobDetails();
		list = this.qrtzJobDetailsDao.selectList(qrtzJobDetails);
		System.out.println(list);
	}

}
