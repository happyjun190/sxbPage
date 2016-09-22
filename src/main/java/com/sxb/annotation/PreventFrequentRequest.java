package com.sxb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止频繁地访问
 * @author shenjun
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PreventFrequentRequest {
	
	/**
	 * 两次请求的最小时间间隔（秒）
	 * @return
	 */
	int interval() default 2;	//缺省2秒钟
}
