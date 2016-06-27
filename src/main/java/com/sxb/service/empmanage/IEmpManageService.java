package com.sxb.service.empmanage;

import java.util.Map;

import com.sxb.commons.json.JsonResult;

public interface IEmpManageService {
	
	/**
	 * 获取用户考勤记录列表
	 * @param map
	 * @return
	 */
	public JsonResult getUserKqInfoList(Map<String, Object> map);
	
	
	/**
	 * 获取用户考勤记录列表--local缓存用
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getUserKqInfoListForCache(int userId);
	
	
	/**
	 * 修改用户考勤信息
	 * @param map
	 * @return
	 */
	public JsonResult editUserKqInfo(Map<String, Object> map);
	

}
