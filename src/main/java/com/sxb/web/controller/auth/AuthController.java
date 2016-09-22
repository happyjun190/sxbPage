package com.sxb.web.controller.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.annotation.Permission;
import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.constants.WebConstants;
import com.sxb.commons.json.JsonResult;
import com.sxb.commons.utils.WebUtils;
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
	
	@Permission(loginReqired=false)
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
	
	

	/**
	 * 注册
	 * 
	 * @param map
	 * @return
	 */
	@Permission(loginReqired=false)
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public JsonResult regist(HttpServletRequest request, @RequestBody Map<String, String> map) {
		try {
			return authService.regist(map);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new JsonResult(ReturnCode.EXCEPTION, "用户注册失败！", null);
		}
	}
	

	/**
	 * 检查验证码是否正确
	 * @param request
	 * @param map
	 * @return
	 */
	@Permission(loginReqired=false)
	@RequestMapping(value = "/verifyCaptcha", method = RequestMethod.POST)
	public JsonResult verifyCaptcha(HttpServletRequest request, @RequestBody Map<String, String> map) {
		try {
			return authService.verifyCaptcha(request, map);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new JsonResult(ReturnCode.EXCEPTION, "检查验证码失败！", null);
		}
	}
	
	/**
	 * 用户登录（web）
	 * 
	 * @param map（userName, password）
	 * @return
	 */
	@Permission(loginReqired=false)
	@RequestMapping(value = "/webLogin", method = RequestMethod.POST)
	public JsonResult webLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> map) {
		try {
			//获取验证码的cookie id
			//String captchaCookie = WebUtils.getCookieByName(request, WebConstants.CookieName.Captcha);
			//map.put("captchaCookie", captchaCookie);
			
			//一些返回的cookie放在response中
			return authService.webLogin(map, response, true);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new JsonResult(ReturnCode.EXCEPTION, "用户登录失败！", null);
		}
	}
	
	/**
	 * 退出登录（web）
	 * 
	 * @param map（userName, password）
	 * @return
	 */
	@Permission(loginReqired=false)
	@RequestMapping(value = "/webLogout", method = RequestMethod.POST)
	public JsonResult webLogout(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map) {
		try {
			map.put("token", WebUtils.getCookieByName(request, WebConstants.CookieName.Token));
			return authService.webLogout(map);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new JsonResult(ReturnCode.EXCEPTION, "退出登录失败！", null);
		}
	}
	
}
