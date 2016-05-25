package com.sxb.commons.json;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JsonResult {
	
	private String code;
	private String message;
	private Object data;
	private Object hint;
	
	public JsonResult() {
		super();
	}

	public JsonResult(String code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	public JsonResult(String code, String message, Object data,Object hint) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.hint = hint;
	}
	
	public Object getHint() {
		return hint;
	}

	public void setHint(Object hint) {
		this.hint = hint;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
