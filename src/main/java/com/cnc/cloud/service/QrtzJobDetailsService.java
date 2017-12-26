package com.cnc.cloud.service;

import java.util.List;
import java.util.Map;


import com.cnc.cloud.bean.QrtzJobDetails;

public interface QrtzJobDetailsService {
	//增删改
	Map<String, Object> createQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception;
	Map<String, Object> createQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception;
	
	Map<String, Object> updateQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception;
	Map<String, Object> updateQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception;
	
	Map<String, Object> deleteQrtzJobDetails(QrtzJobDetails qrtzJobDetails) throws Exception;
	Map<String, Object> deleteQrtzJobDetails(List<QrtzJobDetails> qrtzJobDetailsList) throws Exception;
	
	//查询
	QrtzJobDetails findQrtzJobDetailsByPrimaryKey(String id);
	List<Map<String, Object>> findMapList(QrtzJobDetails qrtzJobDetails);
	List<QrtzJobDetails> findList(QrtzJobDetails qrtzJobDetails);
	

	
}
