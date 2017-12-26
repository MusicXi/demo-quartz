package com.cnc.cloud.dao;

import java.util.List;
import java.util.Map;

import com.myron.db.mybatis.annotation.MyBatisRepository;


import com.cnc.cloud.bean.QrtzJobDetails;

@MyBatisRepository
public interface QrtzJobDetailsDao {
	//增加记录
	int insert(QrtzJobDetails qrtzJobDetails);
	int insertSelective(QrtzJobDetails qrtzJobDetails);
	int insertByBatch(List<QrtzJobDetails> list);
	int insertSelectiveByBatch(List<QrtzJobDetails> list);
	//删除记录
	int deleteByPrimaryKey(String id);
	
	//修改记录
	int updateByPrimaryKey(QrtzJobDetails qrtzJobDetails);
	int updateByPrimaryKeySelective(QrtzJobDetails qrtzJobDetails);
	int updateByBatch(List<QrtzJobDetails> list);
	int updateSelectiveByBatch(List<QrtzJobDetails> list);
	
	//查询记录
	QrtzJobDetails selectByPrimaryKey(String id);
	
	//查询记录列表
	List<QrtzJobDetails> selectList(QrtzJobDetails qrtzJobDetails);

	
	List<Map<String, Object>> selectMapList(QrtzJobDetails qrtzJobDetails);


}
