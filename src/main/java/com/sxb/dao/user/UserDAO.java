package com.sxb.dao.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.sxb.model.user.TabUser;
import com.sxb.model.user.TabUserKq;
import com.sxb.repositories.UserRepository;

@UserRepository
public interface UserDAO {
	
	public TabUser getUserInfo(Map<String, Object> map);
	
	
	@Select("select id, user_id, user_name, position, sex,address,ctime,mtime from ts_user_kq ")
	public List<TabUserKq> getUserKqList();
	
}
