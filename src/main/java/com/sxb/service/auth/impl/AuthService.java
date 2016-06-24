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
		String password = (String) map.get("password");
		TabUser tabUser = userDAO.getUserInfo(map);
		if(tabUser==null||!tabUser.getPassword().equals(password)) {
			return new JsonResult(ReturnCode.PARAMSERROR,"账户名活密码错误,请重新登陆",null);
		}
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("userInfo", tabUser);
		
		return new JsonResult(ReturnCode.SUCCESS,"用户登陆成功",resultMap);
	}

}
