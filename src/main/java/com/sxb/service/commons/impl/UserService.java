package com.sxb.service.commons.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sxb.commons.constants.RedisConstants;
import com.sxb.service.commons.IUserService;
import com.sxb.dao.user.UserDAO;
import com.sxb.commons.cache.IRedisOperator;

/**
 * 
 * @author shenjun
 * @date 2016年09月22日
 */
@Service
public class UserService implements IUserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private IRedisOperator redisOperator;
	@Autowired
	private UserDAO userDAO;

	@Override
	public void ClearUserInfoFromCache(int userId){
		redisOperator.delete(RedisConstants.Prefix.USER_INFO.id()+userId);
	}
	
	@Override
	public int getUserIdByPhone(String phone) {
		Integer result = userDAO.getUserIdByPhone(phone); 
		return (result==null)? -1 : result;
	}
}