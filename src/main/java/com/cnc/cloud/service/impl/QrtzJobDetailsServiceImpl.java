package com.cnc.cloud.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cnc.cloud.bean.QrtzJobDetails;
import com.cnc.cloud.dao.QrtzJobDetailsDao;
import com.cnc.cloud.service.QrtzJobDetailsService;

@Service("qrtzJobDetailsService")
@Transactional(rollbackFor=Exception.class)
public class QrtzJobDetailsServiceImpl  implements QrtzJobDetailsService {
	private static final Logger LOGGER=LoggerFactory.getLogger(QrtzJobDetailsServiceImpl.class);
	
	@Autowired
	private QrtzJobDetailsDao qrtzJobDetailsDao;
	
	@Override
	public Map<String, Object> createQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		int flag = this.qrtzJobDetailsDao.insertSelective(qrtzJobDetails);
		if (flag != 1) {
			LOGGER.error("创建QrtzJobDetails失败! flag={}", flag);
			throw new Exception("创建QrtzJobDetails失败!");
		}
		resultMap.put("success", true);
		resultMap.put("msg", "创建QrtzJobDetails 成功!");
		LOGGER.info("创建QrtzJobDetails 成功 " + qrtzJobDetails.toString());
		return resultMap;
	}
	
	@Override
	public Map<String, Object> createQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();

		if (qrtzJobDetailsList == null || qrtzJobDetailsList.isEmpty()) {
			throw new Exception("无批量新增的数据");
		}
		
		for (QrtzJobDetails qrtzJobDetails : qrtzJobDetailsList) {
			//TODO 可修改主键生成Id方式
//		    qrtzJobDetails.setSchedName(UuidUtils.creatUUID());
			if (this.qrtzJobDetailsDao.insertSelective(qrtzJobDetails) != 1) {
				throw new Exception("新增数据失败!");
			}
		}
		resultMap.put("success", true);
		resultMap.put("msg", "批量创建QrtzJobDetails 成功!");
		return resultMap;

	}
	
	@Override
	public Map<String, Object> updateQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		int flag = this.qrtzJobDetailsDao.updateByPrimaryKeySelective(qrtzJobDetails);
		if (flag != 1) {
			LOGGER.error("更新QrtzJobDetails 失败! flag={}", flag);
			throw new Exception("updateQrtzJobDetails failure!");
		}
		resultMap.put("success", true);
		resultMap.put("msg", "修改QrtzJobDetails 成功!");
		LOGGER.info("修改QrtzJobDetails 成功! " + qrtzJobDetails.toString());
		return resultMap;
	}
	
	@Override
	public Map<String, Object> updateQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		if (qrtzJobDetailsList == null || qrtzJobDetailsList.isEmpty()) {
			throw new Exception("无批量新增的数据");
		}
		
		for (QrtzJobDetails qrtzJobDetails : qrtzJobDetailsList) {
			if (this.qrtzJobDetailsDao.updateByPrimaryKeySelective(qrtzJobDetails) != 1){
				throw new Exception("修改数据失败!");
			}
		}
		resultMap.put("success", true);
		resultMap.put("msg", "批量修改QrtzJobDetails 成功!");
		return resultMap;
	}


	@Override
	public Map<String, Object> deleteQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		//TODO 设置主键ID
	    String id = qrtzJobDetails.getSchedName();
		int flag = this.qrtzJobDetailsDao.deleteByPrimaryKey(id);
		if (flag != 1) {
			LOGGER.error("删除QrtzJobDetails 失败! flag={}", flag);
			throw new Exception("deleteQrtzJobDetails failure!");
		}
		resultMap.put("success", true);
		resultMap.put("msg", "删除QrtzJobDetails 成功!");
		LOGGER.info("删除QrtzJobDetails 成功 key:{}", id);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> deleteQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		int count = 0;
		
		if (qrtzJobDetailsList == null || qrtzJobDetailsList.isEmpty()) {
			throw new Exception("无批量删除的数据!");
		}
		
		for (QrtzJobDetails qrtzJobDetails : qrtzJobDetailsList) {
			//TODO 设置主键ID
		    String id = qrtzJobDetails.getSchedName();
			if (this.qrtzJobDetailsDao.deleteByPrimaryKey(id) != 1) {
				throw new Exception("删除数据失败!");
			}
			count++;
		}
		resultMap.put("success", true);
		resultMap.put("msg", "批量删除QrtzJobDetails 成功!");
		resultMap.put("count", count);
		return resultMap;
	}
	
	@Override
	public QrtzJobDetails findQrtzJobDetailsByPrimaryKey(String id) {
		return this.qrtzJobDetailsDao.selectByPrimaryKey(id);
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
