package com.sxb.service.auth;

import java.awt.image.BufferedImage;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	
	/**
	 * 注册
	 * 
	 * @param map
	 * @return
	 */
	public JsonResult regist(Map<String, String> map);
	
	
	/**
	 * 用户在Web登录的接口
	 * @param map
	 * @param response：用于设置cookie
	 * @return
	 */
	public JsonResult webLogin(Map<String, String> map,	HttpServletResponse response, boolean captchaRequired);
	
	/**
	 * 退出web登录 
	 * @param map
	 * @return
	 */
	public JsonResult webLogout(Map<String, Object> map);

	
	/**
	 * 生成并返回一个验证码
	 * @param response
	 * @return
	 */
	public BufferedImage genCaptcha(HttpServletResponse response);

	/**
	 * 检查验证码是否正确
	 * @param map
	 * @return
	 */
	public JsonResult verifyCaptcha(HttpServletRequest request, Map<String, String> map);
	
}
