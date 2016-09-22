package com.sxb.commons.cache;

/**
 * 本地缓存接口
 * @author shenjun
 *
 * @param <K>
 * @param <V>
 */
public interface ILocalCache <K, V> {
	
	/**
	 * 从缓存中获取数据
	 * @author shenjun
	 * @param key
	 * @return
	 */
	public V get(K key);
}
