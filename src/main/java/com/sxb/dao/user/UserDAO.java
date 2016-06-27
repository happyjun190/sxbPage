package com.sxb.dao.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sxb.model.user.TabUser;
import com.sxb.model.user.TabUserKq;
import com.sxb.repositories.UserRepository;

@UserRepository
public interface UserDAO {
	
	public TabUser getUserInfo(Map<String, Object> map);
	
	
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
