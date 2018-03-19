package com.cnc.cloud.quartz.cluster;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cnc.cloud.util.SpringContextHolder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/spring-context.xml"})
public class ServcieInfoTest {
	
	@Test(expected = NoSuchMethodException.class) 
	public void testServiceInfo() throws NoSuchMethodException, SecurityException{

//		SpringContextHolder.checkBeanAndMethod("HelloService", "sayHello", null);
	}

}
