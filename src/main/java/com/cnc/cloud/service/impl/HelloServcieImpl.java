package com.cnc.cloud.service.impl;

import org.springframework.stereotype.Service;

import com.cnc.cloud.service.HelloService;

@Service("helloService")
public class HelloServcieImpl implements HelloService {

	@Override
	public void sayHello() {
		System.out.println("hello world, i am quartz");
		
	}

}
