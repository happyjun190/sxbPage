package com.sxb.web.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 拦截器声明
 * @author shenjun
 * 20160111
 */
public class ControllerMethodInterceptor implements MethodInterceptor{

	//log info
	private final static Logger logger = LoggerFactory.getLogger(ControllerMethodInterceptor.class);
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		
		
		logger.info("interceptor method");
		// TODO Auto-generated method stub
		return "success";
	}

}
