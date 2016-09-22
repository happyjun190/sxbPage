package com.sxb.web.interceptor;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.constants.WebConstants;
import com.sxb.commons.json.JsonResult;
import com.sxb.commons.utils.WebUtils;

/**
 * 拦截器：统一检查authcode和usertoken，记录接口调用日志
 * 
 * @author shenjunhenjun
 * 
 */
public class ControllerMethodInterceptor extends BaseInterceptor implements MethodInterceptor {
	
	
	
	/**
	 * 拦截Controller中的方法，处理一些公共逻辑： 
	 * 1、对于用Map接收参数的，检查其中的authcode、usertoken是否有效
	 * 2、对于不用Map接收数据的，需要自己在service中检查authcode和usertoken
	 * 3、对于用Map接收参数，又不需要检查usertoken的 （比如login），需要为方法添加注解@LoginNotRequired
	 * 4、统一捕获异常，并返回ReturnCode.EXCEPTION给前端
	 * 5、记录接口调用日志：数据库记录会截取超长参数，文本log会抛弃图片的base64编码
	 * 
	 * @author shenjun
	 * @param MethodInvocation 被拦截的方法
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//long beginTime = System.currentTimeMillis();
		int userIdInt = -1; //缺省的userId
		String userId=null;
		//TabUserOpLog userOpLog = new TabUserOpLog();
		
		Set<Object> allParams = new LinkedHashSet<>(); //保存到文件中的所有原始的请求参数
		
		Method method = invocation.getMethod();
		String methodName = method.getName();
		/*userOpLog.setMethod(methodName);
		userOpLog.setCtime(beginTime/1000);*/
		
		logger.debug("请求开始，方法：{}", methodName);

		Object result = null;
		
		final Object[] args = invocation.getArguments();
		if (args != null && args.length != 0) {
			for (Object arg : args) {
				if (arg instanceof Map<?, ?>) {
					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) arg;

					// 检查authcode是否正确
					if(!verifyAuthCode(method, map)){
						logger.info("秘钥验证不通过!");
						result = new JsonResult(ReturnCode.ERROR, "秘钥验证不通过！", null);
						//break; //继续走，抓取参数到log
					}
					
					//登录接口，用其他方法获取userId，如果获取不到则为“-1”
					try {
						switch (methodName) {
						case "login":
						case "webLogin":
							userIdInt = userService.getUserIdByPhone((String)map.get("userAccount"));
							userId = String.valueOf(userIdInt);
							if(userIdInt!=-1){
								userService.ClearUserInfoFromCache(userIdInt); //清除缓存数据
							}
							break;
						default:
							break;
						}
					} catch (Exception e) {
						logger.error("根据登录账号获取userIdInt时发生异常。", e);
					}
					
					//如果userId已经有了（已经通过web cookie获取到，或者通过登录方法获取到），则不用从map中获取
					if(StringUtils.isBlank(userId)){
						//提前尝试获取userId，以便保存到用户日志中
						String usertoken = (String) map.get("usertoken");
						if(StringUtils.isNotBlank(usertoken)){
							userId = redisService.getUseridByUsertoken(usertoken); // 根据UserToken获取userId
						}
					}
					
					//获得userIdInt：
					if(StringUtils.isNotBlank(userId) && userIdInt==-1){
						//map.put("userid", userId);
						try {
							userIdInt = Integer.parseInt(userId);
							//map.put("userIdInt", userIdInt);
						} catch (Exception e) {
							logger.error("转换userId为int时发生了异常：userId={}", userId, e);
						}
					}
					map.put("userId", userId);
					map.put("userIdInt", userIdInt);

					//提前获取前端类型
					String version = map.get("version")==null?"":map.get("version").toString();
					map.put("version", version);
					
					//提前获取前端设备类型：
					String platform = (String)map.get("platform");//==null?"":map.get("plateform").toString();
					if(StringUtils.isBlank(platform)){
						platform = map.get("plateform")==null?"unknown":map.get("plateform").toString();
						map.put("platform", platform);
					}
					
					if(isLoginRequired(method) && userIdInt==-1) { // 该接口需要登录却没有登录……
						result = new JsonResult(ReturnCode.NOTLOGIN, "用户未登录或已经过期，请重新登录。", null);
					}else if(isTooFrequentRequest(invocation.getMethod(), userId)){ // 如果是太频繁的请求……
						result = new JsonResult(ReturnCode.ERROR, "操作太快了，休息几秒再试吧。", null);
					}
					
					if(map.size()>0){
						allParams.add(map);
					}
				}else if(arg instanceof HttpServletRequest){
					HttpServletRequest request = (HttpServletRequest) arg;
					
					//通过cookie中的token从redis中获取userId
					String webToken = WebUtils.getCookieByName(request, WebConstants.CookieName.Token);
					if(StringUtils.isNotBlank(webToken)){
						userId = redisService.getUserIdByWebToken(webToken); //根据webToken获取userId
						logger.debug("Got userId via webToken, userId={}", userId);
					}	
					
					//获取请求方的IP地址到log中
					/*String ip = WebUtils.getIpAddress(request);
					userOpLog.setLoginIp(ip);
					
					//获取url到log中：
					userOpLog.setUrl(request.getRequestURL().toString());
					*/
					//获取query string 或 posted form data参数
					Map<String, String[]> paramMap = request.getParameterMap();
					if(paramMap!=null && paramMap.size()>0){
						//Map<String, String[]> copyMap = new HashMap<>();
						//copyMap.putAll(paramMap);
						allParams.add(paramMap);
					}
				}else if(arg instanceof HttpServletResponse){
					//do nothing...
				}else if(arg != null){ //其他用对象接收的参数
					allParams.add(arg);
				}
			}
		}
		
		try {
			if(result == null){
				// 一切正常的情况下，继续执行被拦截的方法
				result = invocation.proceed();
			}
		} catch (Exception e) {
			logger.error("Exception caught by Method Interceptor!", e);
			result = new JsonResult(ReturnCode.EXCEPTION, e.getMessage(), null);
		}
		
		/*if(result instanceof JsonResult){
			userOpLog.setResult((JsonResult)result);
		}

		userOpLog.setUserId(userIdInt);
		userOpLog.setAllParams(allParams);
		userOpLog.setCostMs(System.currentTimeMillis() - beginTime);*/

		//log每个用户请求到数据库，异步处理
		//logService.saveUserOpLog(userOpLog, "userOpLog");
		
		return result;
	}
	
	
}