package com.cnc.cloud.service;  
  
import org.springframework.stereotype.Service;
  
  
@Service("simpleService")  
public class SimpleService {  
    
    public String testMethod1(){  
        //这里执行定时调度业务  
        return "testMethod1.....执行1.....";  
    }  
      
    public String testMethod2(){  
    	return "testMethod2.....执行2.....";    
    }  
}