package com.cnc.cloud.quartz.cluster.example.test;  
  
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试quartz集群模式,启动多个实例即可
 * @author linrx1
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/spring-context.xml", "classpath:spring/spring-context-quartz.xml"})
public class MainTest {  
  
    @Test
    public void test() {

    	while (true) {
    		
    	}
    }
  
}  