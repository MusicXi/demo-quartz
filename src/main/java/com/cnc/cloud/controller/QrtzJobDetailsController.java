package  com.cnc.cloud.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.Page;
import com.cnc.cloud.bean.QrtzJobDetails;
import com.cnc.cloud.service.QrtzJobDetailsService;


@Controller
@RequestMapping("/qrtzJobDetails")
public class QrtzJobDetailsController {
	//private static final Logger logger = LoggerFactory.getLogger(QrtzJobDetailsController.class);
	
	@Autowired
	private QrtzJobDetailsService qrtzJobDetailsService;
	

	@RequestMapping("/listByPage")
	@ResponseBody
	public  Map<String, Object> listByPage(String filter, QrtzJobDetails qrtzJobDetails, Page<Map<String, Object>> page, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		//设置默认排序属性
		//page.setDefaultSort("createTime", "desc");
		page = this.qrtzJobDetailsService.findMapListByPage(qrtzJobDetails, page);
		map.put("studentdata", page);
	    map.put("number", page.getTotal());
		return map;
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public Map<String, Object> addQrtzJobDetails(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.createQrtzJobDetails(qrtzJobDetails);
		
		return map;
	}
	
	@RequestMapping("/edit")
	@ResponseBody
	public Map<String, Object> updateQrtzJobDetails(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.updateQrtzJobDetails(qrtzJobDetails);
		return map;
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public Map<String, Object> deleteQrtzJobDetails(QrtzJobDetails qrtzJobDetails, HttpServletRequest request) throws Exception{
		Map<String, Object> map = new HashMap<>();
		map = this.qrtzJobDetailsService.deleteQrtzJobDetails(qrtzJobDetails);
		return map;
	}
	
}
