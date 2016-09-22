package com.sxb.service.commons;

/**
 * 
 * @author shenjun
 * @date 2016年9月22日
 */
public interface IUserService {
	
	/**
	 * 清除redis中的UserInfo信息
	 * @param userId
	 */
	public void ClearUserInfoFromCache(int userId);
	
	/**
	 * 根据电话号码获取userId
	 * @param phone
	 * @return
	 */
	public int getUserIdByPhone(String phone);


	
}