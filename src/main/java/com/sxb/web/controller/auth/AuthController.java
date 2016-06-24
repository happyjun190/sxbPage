package com.sxb.web.controller.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.json.JsonResult;
import com.sxb.service.auth.IAuthService;

/**
 * 用用户权限验证controller，登陆注册等
 * @author shenjun
 * 2016/05/25
 */
@RestController
@RequestMapping("/servlet/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private IAuthService authService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JsonResult login(HttpServletRequest request, @RequestBody Map<String, Object> map) {
		try {
			logger.info("=====================login success===========");
			return authService.login(map);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new JsonResult(ReturnCode.EXCEPTION, "用户登录失败！", null);
		}
	}
	
	
	
}
