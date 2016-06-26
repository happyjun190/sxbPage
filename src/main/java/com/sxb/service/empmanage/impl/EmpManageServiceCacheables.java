package com.sxb.service.empmanage.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sxb.service.empmanage.IEmpManageService;
import com.sxb.service.empmanage.IEmpManageServiceCacheables;

@Service
public class EmpManageServiceCacheables implements IEmpManageServiceCacheables {

	private static final Logger logger = LoggerFactory.getLogger(EmpManageServiceCacheables.class);
	@Autowired
	private IEmpManageService empManageService;
	
	
	/**
	 * 考勤记录一些不经常变动的数据做缓存
	 */
	@Override
	@Cacheable(			
			value = "getUserKqInfoList", 
			key = "'getUserKqInfoList:' + #userId"
			)
	public Map<String, Object> getUserKqInfoListCacheable(int userId) {
		logger.info("cache expired or new: getUserKqInfoList," + " userId:{}", userId);
		return empManageService.getUserKqInfoListForCache(userId);
	}

}
