package com.sxb.commons.utils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取spring容器，以访问容器中的bean
 * @author XuJijun
 *
 */
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;

	}

	public static ApplicationContext getApplicationContext() {
        return SpringContextUtil.applicationContext;
    }
	
	/**
	 * 根据beanName获取Bean
	 * @param beanName
	 * @return bean
	 * @throws BeansException
	 */
	public static Object getBean(String beanName) throws BeansException {
        return SpringContextUtil.applicationContext.getBean(beanName);
    }
	
	/**
	 * 根据Class获取Bean
	 * @param requiredType 如： aaa.class
	 * @return bean
	 * @throws BeansException
	 */
	public static <T> Object getBean(Class<T> requiredType) throws BeansException {
        return SpringContextUtil.applicationContext.getBean(requiredType);
    }
	
	/**
	 * 根据type获取Bean集合
	 * @param type
	 * @return <name, object>集合
	 */
	public static Map<String, ? extends Object> getBeanOfType(Class<? extends Object> type){
		return SpringContextUtil.applicationContext.getBeansOfType(type);
	}
}
