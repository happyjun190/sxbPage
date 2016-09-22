package com.sxb.commons.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import redis.clients.jedis.Tuple;

public interface IRedisOperator {

	/**
	 * 根据pattern 获取所有的keys
	 * @param pattern
	 * @return
	 */
	TreeSet<String> keys(String pattern);
	
	/**
	 * 获取一个key的类型
	 * @param key
	 * @return
	 */
	String type(String key);
	
	/**
	 * 根据key的time to live
	 * 
	 * @param key
	 * @return
	 */
	public long getTtl(final String key);
	
	/**
	 * 向redis加入一个键值对
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return boolean
	 */
	public boolean set(final String key, final String value);

	/**
	 * 添加key value 并且设置存活时间
	 * 
	 * @param key
	 * @param value
	 * @param liveTime
	 *            单位秒
	 */
	public void set(final String key, final String value, final Integer liveTime);

	// 废弃 by wuyongqi
	// /**
	// * 批量向redis加入键值对,map的键为"key",值为"value"
	// * @param list 一个map代表一行记录
	// * @param temp
	// temp+(map中的key)做为redis中的key,例如usertoken:123456;为空则默认map的key为key,如123456
	// * @return boolean
	// */
	// public boolean setList(final List<Map<String,String>> list,final String
	// temp);

	/**
	 * 根据key获取value
	 * 
	 * @param key
	 * @return
	 */
	public String get(final String key);

	/**
	 * 返回 key 中字符串值的子字符串，字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public String getrange(String key, long start, long end);


	/**
	 * 自增(+1)
	 * 
	 * @param key
	 */
	public Long incr(String key);
	
	/**
	 * 增长一个值
	 * @param key
	 * @param increment
	 * @return
	 */
	public Long incrBy(String key, int increment);
	
	/**
	 * 减去一个值
	 * @param key
	 * @param decrement
	 * @return
	 */
	public Long decrBy(String key, int decrement);
	
	/**
	 * 自减(-1)
	 * 
	 * @param key
	 */
	public Long decr(String key);
	
	/**
	 * 自增(+1)基于hash(哈希表)
	 * 
	 * @param key
	 * @param field
	 */
	public void hincrBy(String key,String field);
	
	/**
	 * 根据key删除value
	 * 
	 * @param key
	 * @return
	 */
	public void delete(String key);

	/**
	 * 返回指定区间内的成员，(0,-1)表示返回全部
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zrange(String key, long start, long end);
	
	/**
	 * 返回指定区间内的成员和score，(0,-1)表示返回全部
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<Tuple> zrangeWithScores(String key, long start, long end); 
	
	/**
	 * 根据一定范围返回value
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zrangeByScore(String key, double min, double max);
	
	/**
	 * 添加SortedSet(有序集合)
	 * 
	 * @param key
	 * @param value
	 * @param score
	 */
	public void zadd(String key, String value, double score);

	/**
	 * 删除SortedSet(有序集合)
	 * 
	 * @param key
	 * @param member
	 */
	public void zrem(String key, String... member);

	/**
	 * hash值存放
	 * 
	 * @param key
	 *            缓存key值
	 * @param field
	 *            hash key值
	 * @param value
	 *            hash value值
	 * @param seconds
	 *            生存时间，秒
	 */
	public void hset(final String key, final String field, final String value, int seconds);
	
	/**
	 * hash值存放
	 * 
	 * @param key
	 *            缓存key值
	 * @param field
	 *            hash key值
	 * @param value
	 *            hash value值
	 */
	public void hset(final String key, final String field, final String value);

	/**
	 * hash值批量存放，没有有效期
	 * @param key
	 * @param hash
	 */
	public void hmset(String key, Map<String, String> hash);
	
	/**
	 * hash值批量存放
	 * @param key
	 * @param hash
	 * @param seconds
	 */
	public void hmset(String key, Map<String, String> hash, int seconds);
	
	/**
	 * hdel删除
	 * 
	 * @param key
	 * @param member
	 */
	public void hdel(String key, String... field);



	/**
	 * hash值获取
	 * 
	 * @param key
	 *            缓存key值
	 * @param field
	 *            hashkey值
	 * @return
	 */
	public String hget(final String key, final String field);

	/**
	 * hash值获取
	 * 
	 * @param key
	 *            缓存key值
	 * @param field
	 *            hashkey值
	 * @return
	 */
	public List<String> hmget(String key, String... fields);

	/**
	 * 获取所有的key-value键值对
	 * @param key
	 * @return
	 */
	public Map<String,String> hgetAll(String key);

	/**
	 * 返回set集合
	 * 
	 * @param key
	 */
	public Set<String> smembers(String key);

	/**
	 * 添加元素到set集合
	 * 
	 * @param key
	 * @param member
	 */
	public void sadd(String key, String member);
	
	/**
	 * List功能
	 * 
	 * @param key
	 * @param fields
	 */
	public void lpush(String key, String... fields);

	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定，-1表示最后一个
	 * @param key
	 * @param start
	 * @param end
	 */
	public List<String> lrange(String key, long start, long end);
	
	/**
	 * 向redis加入一个键值对
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return boolean
	 */
	public boolean setnx(final String key, final String value);
	
	/**
	 * 向redis加入一个键值对
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return boolean
	 */
	public String getSet(final String key, final String value);
	
	/**
	 * 返回有序集中，成员的分数值
	 * @param key
	 * @param member
	 * @return
	 */
	public Double zscore(final String key,final String member); 
	
	/**
	 * 有序集合中对指定成员的分数加上增量 score
	 * @param key 键
	 * @param score 分数
	 * @param member 成员
	 * @return
	 */
	public Double zincrby(final String key, final double score, final String member);
	
	/**
	 * 执行 一个lua 脚本
	 * @param script 脚本
	 * @param keys 键
	 * @param args 参数
	 * @return
	 */
	public Object eval(final String script, final List<String> keys, final List<String> args);
	
	/**
	 * setex
	 * @param key
	 * @param seconds expire time in sec
	 * @param value
	 * @return
	 */
	public String setex(String key, int seconds, String value);
}