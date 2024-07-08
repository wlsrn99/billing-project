package com.streaming.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {

	@Before("@annotation(com.streaming.aop.annotations.ReadOnly)")
	public void setReadDataSourceType() {
		DataSourceContextHolder.setDataSourceType(DataSourceType.READ);
	}

	@Before("@annotation(com.streaming.aop.annotations.WriteOnly)")
	public void setWriteDataSourceType() {
		DataSourceContextHolder.setDataSourceType(DataSourceType.WRITE);
	}
}