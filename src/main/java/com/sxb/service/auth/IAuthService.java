package com.sxb.service.auth;

import java.util.Map;

import com.sxb.commons.json.JsonResult;

/**
 * 用户权限验证service，登陆注册等
 * @author shenjun
 *
 */
public interface IAuthService {
		/**
		 * 用户登录接口
		 * 
		 * @author shenjun
		 * @param map
		 * @return
		 */
		public JsonResult login(Map<String, Object> map);
	
}
