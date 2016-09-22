package com.sxb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sxb.commons.constants.Privilege;

/**
 * 权限控制
 * @author shenjun
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Permission {
	/**
	 * 是否需要登录，缺省为需要
	 * @return
	 */
	boolean loginReqired() default true;
	
	/**
	 * 需要的权限，缺省值为不需要任何权限。如果是多个，表示多个权限都要满足。
	 * @return
	 */
	Privilege[] privilege() default Privilege.ANY;
}
