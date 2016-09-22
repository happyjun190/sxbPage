package com.sxb.web.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sxb.annotation.Permission;
import com.sxb.annotation.PreventFrequentRequest;
import com.sxb.commons.cache.IRedisOperator;
import com.sxb.commons.constants.RedisConstants;
import com.sxb.service.commons.IRedisService;
import com.sxb.service.commons.IUserService;

/**
 * 拦截器基类：定义一些通用的方法
 * 
 * @author XuJijun
 * 
 */
public class BaseInterceptor {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected IRedisService redisService;
	@Autowired
	protected IUserService userService;
	@Autowired
	protected IRedisOperator redisOperator;
	

	protected String authcode="123456";
	
	/**
	 * 判断是否太频繁的请求
	 * @param method
	 * @param userId
	 * @return
	 */
	protected boolean isTooFrequentRequest(Method method, String userId){
		//如果方法没有@PreventFrequentRequest标注，或者用户id为空，则不检查是否太频繁的请求
		if(!method.isAnnotationPresent(PreventFrequentRequest.class) || StringUtils.isBlank(userId)){
			return false;
		}
		
		//通过缓存中的一个标记（key=" ysb.req:<userId>.<method>"; value="1"），判断是否该用户刚刚请求过这个方法
		StringBuilder redisKey = new StringBuilder(RedisConstants.Prefix.REQUEST.id());
		redisKey.append(userId).append(".").append(method.getName());
		
		String redisValue = redisOperator.get(redisKey.toString());
		
		if("1".equals(redisValue)){
			return true;
		}else {
			redisOperator.set(redisKey.toString(), "1", method.getAnnotation(PreventFrequentRequest.class).interval());
			return false;
		}
	}
	

	/**
	 * 判断一个方法是否需要登录
	 * @param invocation
	 * @return
	 */
	protected boolean isLoginRequired(Method method){
		boolean result = true;
		if(method.isAnnotationPresent(Permission.class)){
			result = method.getAnnotation(Permission.class).loginReqired();
		}
		
		return result;
		//return !method.isAnnotationPresent(LoginNotRequired.class);
	}
	
	/**
	 * 获取api的version，按照约定，获取最后一个String参数
	 * @param args
	 * @return
	 */
	protected String getApiVersion(Object[] args){
		String v = null;
		
		if(args!=null && args.length>0){
			Object lastArg = args[args.length-1];
			if(lastArg instanceof String){
				v = (String) lastArg;
			}
		}
		return v;
	}
	
	
	/**
	 * 鉴权，判断对某个方法的调用是否使用了有效的appId和authCode
	 * @param invocation 被调用的方法
	 * @param appId (map中)
	 * @param authCode (map中)
	 * @return true: 通过；false: 不通过
	 */
	protected boolean verifyAuthCode(Method method, Map<String, Object> map) {
		
		
		String authCode = (String) map.get("authcode");
		
		if (StringUtils.isBlank(authCode)) {
			logger.warn("错误，authcode不能为空！");
			return false;
		}
		
		//判断authCode是否hardcode的值
		return this.authcode.equals(authCode);
	}
	
}