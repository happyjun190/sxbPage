package com.sxb.dao.user;

import java.util.Map;

import com.sxb.model.user.TabUser;
import com.sxb.repositories.UserRepository;

@UserRepository
public interface UserDAO {
	
	public TabUser getUserInfo(Map<String, Object> map);
	
}
