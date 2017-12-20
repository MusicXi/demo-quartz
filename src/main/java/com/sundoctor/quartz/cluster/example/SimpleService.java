package com.sundoctor.quartz.cluster.example;  
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
  
  
@Service("simpleService")  
public class SimpleService {  
      
    private static final Logger logger = LoggerFactory.getLogger(SimpleService.class);  
      
    public void testMethod1(){  
        //这里执行定时调度业务  
        logger.info("testMethod1.....5秒.....");  
    }  
      
    public void testMethod2(){  
        logger.info("testMethod2............10秒");  
    }  
}