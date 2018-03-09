package com.tbj.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCredit {

	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		System.out.println("start");
		/*Credit1ServiceImpl bean = context.getBean(Credit1ServiceImpl.class);
		Credit1 model = new Credit1();
		model.setCreditLine(new BigDecimal(10));
		model.setCompanyId("1001");
		bean.update(model);*/
		
	}
	
}
