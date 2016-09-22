package com.sxb.service.commons.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sxb.commons.cache.IRedisOperator;
import com.sxb.commons.constants.RedisConstants;
import com.sxb.service.commons.IRedisService;

@Service
public class RedisService implements IRedisService{
	private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

	@Autowired
	private IRedisOperator redisOperator;
	
	public String getUseridByUsertoken(String usertoken) {
		String userid = null;
		if(StringUtils.isNotBlank(usertoken)){
			String temp = RedisConstants.Prefix.APP_TOKEN + usertoken;
			logger.debug("开始获取key为{}的userid...", temp);
			userid = redisOperator.get(temp);
			if(userid!=null){
				logger.debug("获取userid成功,userid为{}", userid);
			}else{
				userid = "";
				logger.info("获取userid为空，usertoken：{}", usertoken);
			}
		}else{
			logger.info("usertoken为空,无法获取userid...");
		}
		return userid;
	}
	

	/**
	 * 根据Web token获取userId
	 */
	@Override
	public String getUserIdByWebToken(String webToken) {
		String userId = null;
		if(StringUtils.isNotBlank(webToken)){
			String key = RedisConstants.Prefix.WEB_TOKEN + webToken;
			//userId = redisOperator.get(key);
			try {
				userId = redisOperator.get(key);
			} catch (Exception e) {
				redisOperator.delete(key);
				logger.warn("清除旧版本的key：{}",key);
			}
			
			
			if(StringUtils.isNotBlank(userId)){
				logger.debug("获取userid成功,userid为{}", userId);
			}else{
				userId = "";
				logger.info("获取userid为空，webToken：{}", webToken);
			}
		}else{
			logger.info("Web token为空,无法获取userId...");
		}
		return userId;
	}

}
