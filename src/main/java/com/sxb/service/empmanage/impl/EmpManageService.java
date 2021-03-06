package com.sxb.service.empmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.json.JsonResult;
import com.sxb.dao.user.UserDAO;
import com.sxb.model.user.TabUserKq;
import com.sxb.service.empmanage.IEmpManageService;
import com.sxb.service.empmanage.IEmpManageServiceCacheables;

/**
 * 员工管理模块
 * @author shenjun
 * 2016/06/24
 */
@Service
public class EmpManageService implements IEmpManageService {
	
	//private static final Logger logger = LoggerFactory.getLogger(EmpManageService.class);
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private IEmpManageServiceCacheables empManageServiceCacheables;
	
	@Override
	public JsonResult getUserKqInfoList(Map<String, Object> map) {
		int userId = 10;
		Map<String, Object> resultMap = empManageServiceCacheables.getUserKqInfoListCacheable(userId);
		return new JsonResult(ReturnCode.SUCCESS,"获取用户考勤记录成功",resultMap);
	}

	@Override
	public Map<String, Object> getUserKqInfoListForCache(int userId) {
		Map<String, Object> resultMap = new HashMap<>();
		List<TabUserKq> userKqInfoList = userDAO.getUserKqList();
		resultMap.put("userKqInfoList", userKqInfoList);
		return resultMap;
	}

	@Override
	public JsonResult editUserKqInfo(Map<String, Object> map) {
		
		int kqId = (int) map.get("itemId");
		String userName = (String) map.get("userName");
		String position = (String) map.get("position");
		String address = (String) map.get("address");
		
		TabUserKq tabUserKq = userDAO.selectUserKqInfoForUpdate(kqId);
		tabUserKq.setUser_name(userName);
		tabUserKq.setPosition(position);
		tabUserKq.setAddress(address);
		userDAO.updateUserKqInfo(tabUserKq);
		return new JsonResult(ReturnCode.SUCCESS,"修改用户考勤记录成功",null);
	}

	
	
	
}
