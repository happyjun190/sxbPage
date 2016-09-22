package com.sxb.commons.cache.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.sxb.commons.cache.IRedisOperator;

@Service
public class RedisOperator implements IRedisOperator {

	public final static Logger logger = LoggerFactory.getLogger(RedisOperator.class);

	@Autowired
	private JedisCluster jedisCluster;

	@Override
	public TreeSet<String> keys(String pattern){
		logger.debug("Start getting keys...");
		TreeSet<String> keys = new TreeSet<>();
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for(String k : clusterNodes.keySet()){
			logger.debug("Getting keys from: {}", k);
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = null;
			try {
				connection = jp.getResource();
				keys.addAll(connection.keys(pattern));
			}catch(JedisConnectionException e){
				logger.error("Getting connection error: {}, clusterNode key={}", e.getMessage(), k);
			}catch(Exception e){
				logger.error("Getting keys error: {}", e);
			} finally{
				//logger.debug("Connection closed.");
				if(connection!=null){
					connection.close();
				}
			}
		}
		logger.debug("Keys gotten!");
		return keys;
	}

	@Override
	public String type(final String key) {
		return jedisCluster.type(key);
		
	}

	@Override
	public long getTtl(final String key) {
		return jedisCluster.ttl(key);
	}
	
	@Override
	public boolean set(final String key, final String value) {
		// 转到jedisCluster实现
		return "OK".equals(jedisCluster.set(key, value));
	}

	@Override
	public String get(final String key) {
		return jedisCluster.get(key);
	}

	@Override
	public String getrange(final String key, long start, long end) {
		return jedisCluster.getrange(key, start, end);
	}
	
	@Override
	public void set(String key, String value, Integer liveTime) {
		jedisCluster.setex(key, liveTime, value);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public Long incrBy(String key, int increment) {
		return jedisCluster.incrBy(key, increment);
	}
	
	@Override
	public Long decr(String key) {
		return jedisCluster.decr(key);
	}
	
	@Override
	public void hincrBy(String key,String field) {
		jedisCluster.hincrBy(key, field, 1);
	}
	
	@Override
	public void delete(String key) {
		jedisCluster.del(key);
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		return jedisCluster.zrange(key, start, end);
	}
	
	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		return jedisCluster.zrangeWithScores(key, start, end);
	}
	
	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return jedisCluster.zrangeByScore(key, min, max);
	}
	
	@Override
	public void zadd(String key, String value, double score) {
		jedisCluster.zadd(key, score, value);
	}
	
	@Override
	public void zrem(String key, String... member) {
		jedisCluster.zrem(key, member);
	}

	@Override
	public void hset(String key, String field, String value, int seconds) {
		jedisCluster.hset(key, field, value);
		if (seconds > 0) {
			jedisCluster.expire(key, seconds);
		}
	}

	@Override
	public void hmset(String key, Map<String, String> hash) {
		jedisCluster.hmset(key, hash);
	}

	
	@Override
	public void hmset(String key, Map<String, String> hash, int seconds) {
		jedisCluster.hmset(key, hash);
		if (seconds > 0) {
			jedisCluster.expire(key, seconds);
		}
	}

	@Override
	public void hdel(String key, String... field) {
		jedisCluster.hdel(key, field);
	}


	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return jedisCluster.hmget(key, fields);
	}

	@Override
	public Map<String,String> hgetAll(String key){
		return jedisCluster.hgetAll(key);
	}

	@Override
	public Set<String> smembers(String key) {
		return jedisCluster.smembers(key);
	}

	@Override
	public void sadd(String key, String member) {
		jedisCluster.sadd(key, member);
	}

	@Override
	public void lpush(String key, String... fields) {
		jedisCluster.lpush(key, fields);
	}
	
	@Override
	public List<String> lrange(String key, long start, long end) {
		return jedisCluster.lrange(key, start, end);
	}

	@Override
	public boolean setnx(final String key,final String value) {
		return jedisCluster.setnx(key, value) > 0;
	}

	@Override
	public String getSet(final String key,final String value) {
		return jedisCluster.getSet(key, value);
	}

	@Override
	public Double zscore(final String key,final String member) {
		return jedisCluster.zscore(key, member);
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return jedisCluster.zincrby(key, score, member);
	}

	@Override
	public Long decrBy(String key, int decrement) {
		return jedisCluster.decrBy(key, decrement);
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		return jedisCluster.eval(script, keys, args);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return jedisCluster.setex(key, seconds, value);
	}

	@Override
	public void hset(String key, String field, String value) {
		jedisCluster.hset(key, field, value);
	}
}