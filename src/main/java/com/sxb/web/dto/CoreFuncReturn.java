package com.sxb.web.dto;

public class CoreFuncReturn extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8665033531045581891L;
	public boolean isOK;
	public String msg;
	public Object tag;

	public CoreFuncReturn() {
		this(false);
	}

	public CoreFuncReturn(boolean isOK) {
		this(isOK, null);
	}

	public CoreFuncReturn(boolean isOK, String msg) {
		this(isOK, msg, null);
	}

	public CoreFuncReturn(boolean isOK, String msg, Object tag) {

		this.isOK = isOK;
		this.msg = msg;
		this.tag = tag;
	}

	public void setValues(boolean isok, String msg, Object tag) {
		this.isOK = isok;
		this.msg = msg;
		this.tag = tag;
	}
}
