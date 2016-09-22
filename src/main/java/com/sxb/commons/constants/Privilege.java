package com.sxb.commons.constants;

/**
 * 用户权限：定义用户权限，ts_sys_dict表中的PRIV_TIPS定义了没权限的提示语
 * @author shenjun
 *
 */
public enum Privilege {
	ANY(Category.BLACK_LIST, 999, "", ""),	//不需要任何权限
	
	//登录、用户信息相关模块:
	/** 登录 */
	LOGIN(Category.BLACK_LIST, 100, "login_priv", "login_priv"),
	
	;
	
	public enum Category {
		BLACK_LIST,
		WHITE_LIST,
		CAIGOU_CON,
		STORE,
		;
	}
	
	private Privilege(Category category, int privCode, String colunm, String tipsId) {
		this.setCategory(category);
		this.privCode = privCode;
		this.column = colunm;
		this.tipsId = tipsId;
	}
	
	private Category category; //类别，白名单、黑名单、或其他
	private int privCode; //权限代码
	private String column;	//ts_blackuser_list、ts_whiteuser_list或ts_user中的字段名
	private String tipsId;	//对应于ts_sys_dict中的字段
	
	public String column() {
		return column;
	}

	public String tipsId() {
		return tipsId;
	}

	public int getPrivCode() {
		return privCode;
	}

	public void setPrivCode(int privCode) {
		this.privCode = privCode;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
