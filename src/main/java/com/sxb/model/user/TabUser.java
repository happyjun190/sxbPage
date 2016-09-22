package com.sxb.model.user;

public class TabUser {
	private int id;
	private String login;
	private String password;
	private String phone;
	private String email;
	private int state;
	private int ctime;
	private int mtime;
	
	private String head_url; // 头像地址
	private String real_name;
	private String loginSalt;// 用户密码加密干盐值
	private String qq;// qq
	private int isBindqq;// 是否绑定qq
	private int isBindphone;// 是否绑定手机
	private int isBindemail;// 是否绑定邮箱
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
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
	public String getHead_url() {
		return head_url;
	}
	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getLoginSalt() {
		return loginSalt;
	}
	public void setLoginSalt(String loginSalt) {
		this.loginSalt = loginSalt;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public int getIsBindqq() {
		return isBindqq;
	}
	public void setIsBindqq(int isBindqq) {
		this.isBindqq = isBindqq;
	}
	public int getIsBindphone() {
		return isBindphone;
	}
	public void setIsBindphone(int isBindphone) {
		this.isBindphone = isBindphone;
	}
	public int getIsBindemail() {
		return isBindemail;
	}
	public void setIsBindemail(int isBindemail) {
		this.isBindemail = isBindemail;
	}
	
}
