package com.sxb.model.user;

/**
 * 员工考情表
 * @author shenjun
 *
 */
public class TabUserKq {
	private int id;
	private int user_id;
	private String user_name;
	private String position;
	private int sex;
	private String address;
	private int ctime;
	private int mtime;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getCtime() {
		return ctime;
	}
	public void setCtime(int ctime) {
		this.ctime = ctime;
	}
	public int getMtime() {
		return mtime;
	}
	public void setMtime(int mtime) {
		this.mtime = mtime;
	}
}
