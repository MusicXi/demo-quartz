package com.cnc.cloud.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	
	@RequestMapping("/helloVue2")
	public String index(HttpServletRequest request ){
		//return "/helloVue.jsp";
		return "/index";
	}
	
}
