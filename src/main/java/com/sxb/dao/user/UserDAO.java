package com.sxb.dao.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sxb.model.user.TabUser;
import com.sxb.model.user.TabUserKq;
import com.sxb.repositories.UserRepository;

@UserRepository
public interface UserDAO {
	
	/**
	 * 获取用户信息
	 * @param map
	 * @return
	 */
	public TabUser getUserInfo(Map<String, Object> map);
	
	
	/**
	 * 通过phone获取用户信息
	 * @param phone
	 * @return
	 */
	public TabUser getUserInfoByPhone(String phone);
	
	
	/**
	 * 添加用户账号
	 * @param tabUser
	 * @return
	 */
	public TabUser addUser(TabUser tabUser);
	
	
	/**
	 * 根据电话号码获取用户id
	 * @param phone
	 * @return
	 */
	@Select("SELECT id FROM `ts_user` WHERE phone=#{arg0} limit 1")
	public Integer getUserIdByPhone(String phone);
	
	
	
	/**
	 * 更新用户的登陆账号。。。
	 * 
	 * @param userId
	 * @param login
	 * @param loginSalt
	 * @return
	 */
	@Update(" update ts_user set login = #{login},mtime = UNIX_TIMESTAMP() where id = #{userId}")
	public int updateUserLoginAccount(@Param("userId")String userId, @Param("login")String login);
	
	/**
	 * 获取部门员工考勤记录
	 * @return
	 */
	@Select("select id, user_id, user_name, position, sex,address,ctime,mtime from ts_user_kq ")
	public List<TabUserKq> getUserKqList();
	
	
	/**
	 * 查询用户考勤信息(for update)
	 * @param id
	 * @return
	 */
	@Select("select id, user_id, user_name, position, sex,address,ctime,mtime from ts_user_kq where id = #{id} for update")
	public TabUserKq selectUserKqInfoForUpdate(int id);
	
	
	/**
	 * 修改用户考勤信息
	 * @param tabUserKq
	 */
	@Update("update ts_user_kq set user_name=#{user_name}, position=#{position}, address=#{address} where id = #{id}")
	public void updateUserKqInfo(TabUserKq tabUserKq);
	
	
	
	
}
