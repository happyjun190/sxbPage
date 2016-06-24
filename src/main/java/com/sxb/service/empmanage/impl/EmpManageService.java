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
	
	@Override
	public JsonResult getUserKqInfoList(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		List<TabUserKq> userKqInfoList = userDAO.getUserKqList();
		resultMap.put("userKqInfoList", userKqInfoList);
		return new JsonResult(ReturnCode.SUCCESS,"获取用户考勤记录成功",resultMap);
	}

}
