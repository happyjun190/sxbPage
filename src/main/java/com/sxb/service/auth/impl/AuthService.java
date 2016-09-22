package com.sxb.service.auth.impl;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxb.commons.cache.IRedisOperator;
import com.sxb.commons.constants.RedisConstants;
import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.constants.WebConstants;
import com.sxb.commons.json.JsonResult;
import com.sxb.commons.utils.DateUtils;
import com.sxb.commons.utils.EncryptUtils;
import com.sxb.commons.utils.SecurityUtils;
import com.sxb.commons.utils.WebUtils;
import com.sxb.dao.user.UserDAO;
import com.sxb.model.user.TabUser;
import com.sxb.service.auth.IAuthService;

@Service
public class AuthService implements IAuthService {
	
	/** 数字 */
	public static final String NUMBER = "^[0-9]*$";
	
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private IRedisOperator redisOperator;
	
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
	
	
	
	/**
	 * 注册(目前只支持手机号码注册)
	 * 
	 */
	@Override
	@Transactional
	public JsonResult regist(Map<String, String> map) {
		String phoneNum = map.get("phonenum");// 手机号码
		String password = map.get("password");// 密码
		// String verifyCode = map.get("verifycode");// 验证码
		if (StringUtils.isBlank(phoneNum)) {
			return new JsonResult(ReturnCode.PARAMSERROR, "手机号码不能为空！", null);
		}
		if (StringUtils.isBlank(password)) {
			return new JsonResult(ReturnCode.PARAMSERROR, "密码不能为空！", null);
		}
		if (!phoneNum.matches(NUMBER)) {
			return new JsonResult(ReturnCode.PARAMSERROR, "手机号码输入错误！", null);
		}
		
		// 对加密的明文密码进行解密
		password = EncryptUtils.getPwdFromBase64(password);

		// 根据手机号码判断用户是否已经注册
		TabUser tabUser = userDAO.getUserInfoByPhone(phoneNum);
		if (tabUser != null && tabUser.getIsBindphone() == 1) {
			return new JsonResult(ReturnCode.PARAMSERROR, "该手机号码已经注册过！", null);
		}
		// TODO 校验F密码长度为6至16位，且不能全部为数字

		/**
		 * 注册成功后需要插入的表： 用户表、用户其他信息表、积分表 用户认证信息表、用户账户信息
		 */
		String userName = newGenerateUserName(phoneNum); // 生成用户名
		String loginSalt = SecurityUtils.getRandNumber(5); // md5加密盐值

		// 添加新用户
		int userId = addUser(phoneNum, password, loginSalt, 1);
		if (userId <= 0) {
			return new JsonResult(ReturnCode.ERROR, "注册失败！", null);
		}




		String loginNew = String.valueOf((userId + 1000));
		userDAO.updateUserLoginAccount(String.valueOf(userId), loginNew);

		return new JsonResult(ReturnCode.SUCCESS, "注册成功！", userId);
	}
	
	
	
	// ================================== Web Login 相关分割线  =================================

	/**
	 * 用户web登录，response用于写入token到cookie中
	 */
	@Override
	public JsonResult webLogin(Map<String, String> map, HttpServletResponse response, boolean captchaRequired) {

		// 检查验证码是否已经被验证过
		if (captchaRequired) {
			String cookieId = map.get("captchaCookie");
			
			if(StringUtils.isBlank(cookieId)){
				return new JsonResult(ReturnCode.ERROR, "登录失败：您的浏览器可能不支持Cookie！", null);
			}
			
			String storedCaptcha = redisOperator.hget(RedisConstants.Prefix.CAPTCHA + cookieId, RedisConstants.CapchaInfo.CHECKED.name());
			if (!"1".equals(storedCaptcha)) { //是否已经验证过
				//如果没验证过，是否跟redis中的匹配
				String captchaCode = redisOperator.hget(RedisConstants.Prefix.CAPTCHA + cookieId, RedisConstants.CapchaInfo.CODE.name());
				if(StringUtils.isNotBlank(captchaCode)){
					if(!captchaCode.equalsIgnoreCase(map.get("identification"))){
						return new JsonResult(ReturnCode.ERROR, "登录失败：验证码不对！请重新输入。", null);
					}
				}else {
					return new JsonResult(ReturnCode.ERROR, "登录失败：验证码已经过期！请重新输入。", null);
				}
			}
		}

		// 验证用户名密码
		String userAccount = map.get("userAccount");
		String pw = map.get("password");

		if (StringUtils.isAnyBlank(userAccount, pw)) {
			return new JsonResult(ReturnCode.PARAMSERROR, "用户名或密码不能为空！", null);
		}

		TabUser user = checkUserExist(userAccount, 1); // type=1，只支持手机号码登录

		if(user==null){
			return new JsonResult(ReturnCode.ERROR, "对不起，您尚未注册！", null);
		}
		
		int userId = user.getId();
		
		//TODO
		/*if (!user.getPassword().equals(EncryptUtils.MD5Str(pw + user.getLoginSalt()))) {// 前端送过来的是md5编码后的密码，小写
			return new JsonResult(ReturnCode.ERROR, "用户名或密码错误！", null);
		}*/

		//TODO
		// 判断用户是否已经加入药店
		/*if(!userService.isJoinStore(userId)){
			return new JsonResult(ReturnCode.NO_PRIVILEGE, "您尚未加入药店，请按照右边的操作步骤加入药店后再登录。", null);
		}*/

		//判断用户是否有采购权限：改为注解拦截判断
		/*if(!userService.hasCaigouPrivilege(userId)){
			return new JsonResult(ReturnCode.NO_PRIVILEGE, "您的采购资料正在审核中", null);
		}*/
		
		//TODO
		//boolean isChainHead
		//boolean isChainHead = userService.isChainHead(userId);
		boolean isChainHead = true;
		/*if(!isChainHead){ //非连锁用户，判断该城市是否开通药采购
			Area area = areaService.getCityById((int)userInfoBrief.get("areaId"));
			if (area != null) {
				String citys = sysConfService.getValuesByItem(SysConf.WHOLESALE_CITY);
				if (!citys.contains(String.valueOf(area.getId()))) {
					return new JsonResult(ReturnCode.ERROR, "对不起，您所在的城市尚未开通药店采购服务。", null);
				}
			}
		}*/

		//登录成功……
		
		// 生成一个随机的token
		String token = UUID.randomUUID().toString().replace("-", "");
		String userIdStr = String.valueOf(userId);
		String tokenKey = RedisConstants.Prefix.WEB_TOKEN + token;

		//清除redis中的userInfo
		//userService.ClearUserInfoFromCache(userId); 改为提前在拦截器中执行了
		
		//保存login相关的信息到redis中
		String userLoginInfoKey = RedisConstants.Prefix.USER_LOGIN_INFO + userIdStr;
		Map<String, String> userLoginInfo = new HashMap<>();
		userLoginInfo.put(RedisConstants.UserLoginInfo.WEB_TOKEN.id(), token);
		userLoginInfo.put(RedisConstants.UserLoginInfo.WEB_LOGIN_TIME.id(), DateUtils.getNowTime());
		userLoginInfo.put(RedisConstants.UserLoginInfo.WEB_PLATFORM.id(), (String) map.get("platform"));
		userLoginInfo.put(RedisConstants.UserLoginInfo.WEB_VERSION.id(), (String) map.get("version"));
		redisOperator.hmset(userLoginInfoKey, userLoginInfo);		
		
		//用户信息保存到redis Hash中：userId, uname, headUrl, areaId, rName, rPhone, isChainHead
		//Map<String, String> userInfo = new HashMap<>();
		//userBriefInfo.put("userId", userIdStr);
		//userInfo.put(RedisConstants.UserInfo.IS_CHAIN_HEAD.id(), isChainHead);
		//userInfo.put(RedisConstants.UserInfo.WEB_TOKEN.id(), token);
		/*Map<String, Object> userInfoBrief = userOtherinfoDAO.getUserBriefInfo(user.getId());//获取用户信息
		for(String key : userInfoBrief.keySet()){
			if(key.equals(RedisConstants.UserInfo.HEAD_URL.id())){
				userInfo.put(key, imgUrl + userInfoBrief.get(key));
			}else {
				userInfo.put(key, String.valueOf(userInfoBrief.get(key)));
			}
		}*/
		
		int maxRedisAge = RedisConstants.Prefix.WEB_TOKEN.ttl();// token存进redis，保存一天（单位：分钟）
		int maxCookieAge = 0;//关闭浏览器后cookie就失效（单位：秒）
		if ("on".equals(map.get("rememberMe"))) { // 记住登录状态，5天
			maxRedisAge = RedisConstants.Prefix.WEB_TOKEN.ttl()*5;
			maxCookieAge = maxRedisAge;
		}
		
		//设置token to userId
		redisOperator.set(tokenKey, userIdStr, maxRedisAge);
		
		//设置userInfo Hash
		//redisOperator.hmset(tokenKey, userBriefInfo, maxRedisAge);
		//String userInfoKey = RedisConstants.Prefix.USER_INFO + userIdStr;
		//redisOperator.hmset(userInfoKey, userInfo, maxRedisAge);
		
		WebUtils.addCookie(response, WebConstants.CookieName.Token, token, maxCookieAge);
		WebUtils.addCookie(response, WebConstants.CookieName.IsChainHead, String.valueOf(isChainHead), maxCookieAge);
		
		//String version = map.get("version");
		// 记录登录时间和客户端版本
		//userOtherinfoDAO.updateUserLastLoginTimeAndVersion(userIdStr, version);

		return new JsonResult(ReturnCode.SUCCESS, "登录成功", null);
	}

	/**
	 * 生成一个验证码并保存到redis中
	 */
	@Override
	public BufferedImage genCaptcha(HttpServletResponse response) {

		// 生成一个校验码
		String captcha = WebUtils.genCaptcha(4);

		// 生成一个cookie ID，并塞进response里面
		String cookieId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		WebUtils.addCookie(response, WebConstants.CookieName.Captcha, cookieId, RedisConstants.Prefix.CAPTCHA.ttl());

		// 把校验码、是否已经通过校验（缺省不设置为0）保存到redis中，以cookie ID 为key
		redisOperator.hset(RedisConstants.Prefix.CAPTCHA + cookieId, RedisConstants.CapchaInfo.CODE.name(), captcha,
				RedisConstants.Prefix.CAPTCHA.ttl());

		// 把校验码转为图像
		BufferedImage image = WebUtils.genCaptchaImg(captcha);
		return image;
	}

	/**
	 * 检查验证码是否正确，并把结果写入redis中
	 */
	@Override
	public JsonResult verifyCaptcha(HttpServletRequest request, Map<String, String> map) {

		String receivedCaptcha = map.get("captcha");

		if (StringUtils.isNotBlank(receivedCaptcha)) {
			String cookieId = WebUtils.getCookieByName(request, WebConstants.CookieName.Captcha);
			String storedCaptcha = redisOperator.hget(RedisConstants.Prefix.CAPTCHA + cookieId, RedisConstants.CapchaInfo.CODE.name());
			if (receivedCaptcha.toUpperCase().equals(storedCaptcha)) {
				redisOperator.hset(RedisConstants.Prefix.CAPTCHA + cookieId, RedisConstants.CapchaInfo.CHECKED.name(), "1",
						RedisConstants.Prefix.CAPTCHA.ttl());
				return new JsonResult(ReturnCode.SUCCESS, "有效的验证码。", true);
			} else {
				redisOperator.hset(RedisConstants.Prefix.CAPTCHA + cookieId, RedisConstants.CapchaInfo.CHECKED.name(), "0",
						RedisConstants.Prefix.CAPTCHA.ttl());
			}
		}

		return new JsonResult(ReturnCode.SUCCESS, "无效的验证码。", false);
	}
	
	
	/**
	 * 检查用户是否存在
	 * 
	 * @param $loginAccount
	 *            登录帐号：手机号码、邮箱、QQ号码、Q药网旧用户
	 * @param $type
	 *            登录类型：1-手机号码 2-邮箱 3-QQ号码 4-Q药网旧用户
	 * @return false、null：用户不存在 Array()：用户存在
	 */
	private TabUser checkUserExist(String loginAccount, int type) {
		if (type < 1 || type > 4)
			return null;
		Map<String, Object> map = new HashMap<String, Object>();
		if (type == 1) { // 手机号码登录
			map.put("phone", loginAccount);
		} else if (type == 2) { // 邮箱登录
			map.put("email", loginAccount);
		} else if (type == 3) { // QQ号码
			map.put("qq", loginAccount);
		} else { // Q药网旧用户
			map.put("qdrug_acc", loginAccount);
		}
		return userDAO.getUserInfo(map);
	}


	@Override
	public JsonResult webLogout(Map<String, Object> map) {
		String tokenKey = RedisConstants.Prefix.WEB_TOKEN.id() + map.get("token");
		redisOperator.delete(tokenKey);
		return new JsonResult(ReturnCode.SUCCESS, "已经成功退出登录。", null);
	}
	
	
	/**
	 * 注册时生成用户的昵称(新方法)
	 * 
	 * @param $nameSuffix
	 *            用户名后缀
	 * @return String 用户的昵称
	 */
	private String newGenerateUserName(String nameSuffix) {
		String zero = "00000";
		int minLen = zero.length(); // 最少5位
		int nameLen = nameSuffix.length();
		if (nameLen < minLen) {
			nameSuffix = zero.substring(0, minLen - nameLen) + nameSuffix;
		} else {
			nameSuffix = nameSuffix.substring(nameLen - minLen, nameLen);
		}
		return "药师" + nameSuffix;
	}

	
	
	private int addUser(String loginAccount, String password, String loginSalt, int type) {
		TabUser tabUser = new TabUser();
		if (type < 1 || type > 3)
			return 0;
		if (type == 1) { // 手机号码
			tabUser.setPhone(loginAccount);
			tabUser.setIsBindphone(1);// 注册时绑定手机号码
		} else if (type == 2) { // 邮箱
			tabUser.setEmail(loginAccount);
			tabUser.setIsBindemail(1);// 注册时绑定邮箱
		} else {
			tabUser.setQq(loginAccount);
			tabUser.setIsBindqq(1);// 注册时绑定QQ号码
		}
		password = EncryptUtils.encryptPassword(password, loginSalt);
		tabUser.setPassword(password);
		tabUser.setLoginSalt(loginSalt);
		userDAO.addUser(tabUser);
		return tabUser.getId();
	}
	
}
