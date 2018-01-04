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
	
	/**
	 * 导出Excel文件
	 * @param req
	 * @param resp
	 * @param model
	 */
/*	@RequestMapping("/exportExcel")
	public void exportExcel(HttpServletRequest req, HttpServletResponse resp, ModelMap model){
		
		resp.setContentType("application/x-msdownload");
		try {
			ServletOutputStream output=resp.getOutputStream();
			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = String.valueOf(simpleDateFormat.format(date));
			String fileName = StringUtils.toUtf8String("QrtzJobDetails_"+dateStr);
			//String fileName = "Test";
			resp.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".xlsx\"");
			
			List<Map<String,Object>> fieldData = this.qrtzJobDetailsService.findMapList(new QrtzJobDetails());
			Map<String,String> fieldName = new LinkedHashMap<String,String>();
			fieldName.put("schedName", "无列名");
			fieldName.put("jobName", "无列名");
			fieldName.put("jobGroup", "无列名");
			fieldName.put("description", "无列名");
			fieldName.put("jobClassName", "无列名");
			fieldName.put("isDurable", "无列名");
			fieldName.put("isNonconcurrent", "无列名");
			fieldName.put("isUpdateData", "无列名");
			fieldName.put("requestsRecovery", "无列名");
			fieldName.put("jobData", "无列名");
			ExcelUtil.export(fieldName, fieldData, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * excel导入
	 * TODO 导入成功回调提示
	 * @param req
	 * @param file
	 * @param model
	 * @throws IOException
	 */
/*	@RequestMapping("/importExcel")
	@ResponseBody
	public Map<String, Object> importExcel(HttpServletRequest req, 
			@RequestParam(value="excelFile", required=false) MultipartFile file) throws Exception{
		logger.debug("执行导入操作开始");
		Map<String, Object> map = new HashMap<>();
		CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) file;
		try {
			List<Map<String,Object>> list = ExcelUtil.readExcelData(commonsMultipartFile);
			for (Map<String,Object> obj : list) {
				QrtzJobDetails qrtzJobDetails = new QrtzJobDetails();
				BeanUtils.populate(qrtzJobDetails, obj);
				//TODO 插入主键
				this.qrtzJobDetailsService.createQrtzJobDetails(qrtzJobDetails);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		map.put("tip", "导入成功啦啦啦");
		map.put("success", true);
		return map;
	}*/

	/**
	 * 下载模板
	 * @param model
	 * @param req
	 * @param resp
	 */
/*	@SuppressWarnings("unchecked")
	@RequestMapping("/downloadTemplate")
	public void downloadTemplate(String model,HttpServletRequest req, HttpServletResponse resp){
		Map<String, String> fieldName;
		try {
			if (model != null && ! "".equals(model)) {
				ObjectMapper mapper = new ObjectMapper();
				fieldName = mapper.readValue(model, LinkedHashMap.class);								
			} else {
				fieldName = new LinkedHashMap<String,String>();
				fieldName.put("description", "无列名");				
				fieldName.put("jobClassName", "无列名");				
				fieldName.put("isDurable", "无列名");				
				fieldName.put("isNonconcurrent", "无列名");				
				fieldName.put("isUpdateData", "无列名");				
				fieldName.put("requestsRecovery", "无列名");				
				fieldName.put("jobData", "无列名");				
			}
			ServletOutputStream outputStream;
			
			outputStream = resp.getOutputStream();
			String fileName = StringUtils.toUtf8String("qrtzJobDetailsTemplate");

			resp.setContentType("application/x-msdownload");
			resp.setHeader("Content-Disposition","attachment;filename=\"" + fileName + ".xlsx\"");

			ExcelUtil.exportTemplate(fieldName, outputStream);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
	*/
	
	
	
	
}
