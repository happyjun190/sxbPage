package com.sxb.service.auth.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.json.JsonResult;
import com.sxb.dao.user.UserDAO;
import com.sxb.model.user.TabUser;
import com.sxb.service.auth.IAuthService;

@Service
public class AuthService implements IAuthService {
	
	@Autowired
	private UserDAO userDAO;
	
	@Override
	public JsonResult login(Map<String, Object> map) {
		
		TabUser tabUser = userDAO.getUserInfo(map);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("userInfo", tabUser);
		
		return new JsonResult(ReturnCode.SUCCESS,"",resultMap);
	}

}
