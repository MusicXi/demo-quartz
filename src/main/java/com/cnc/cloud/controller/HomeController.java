package com.cnc.cloud.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {
	
	@RequestMapping("/helloVue2")
	public String index(HttpServletRequest request ){
		//return "/helloVue.jsp";
		return "/index";
	}

	@RequestMapping("/user")
	@ResponseBody
	public Map<String, Object> user(){
		Map<String, Object> result = new HashMap<>();
		return result;
	}
	
}
