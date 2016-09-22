package com.sxb.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisClusterConfig {
	
	@Value("${redis.cluster.host1}") private String HOST1;
	@Value("${redis.cluster.port1}") private Integer PORT1;
	
	@Value("${redis.cluster.host2}") private String HOST2;
	@Value("${redis.cluster.port2}") private Integer PORT2;
	
	@Value("${redis.cluster.host3}") private String HOST3;
	@Value("${redis.cluster.port3}") private Integer PORT3;
	
	@Value("${redis.cluster.host4}") private String HOST4;
	@Value("${redis.cluster.port4}") private Integer PORT4;
	
	@Value("${redis.cluster.host5}") private String HOST5;
	@Value("${redis.cluster.port5}") private Integer PORT5;
	
	@Value("${redis.cluster.host6}") private String HOST6;
	@Value("${redis.cluster.port6}") private Integer PORT6;
	
	@Bean
	public JedisCluster jedisCluster() {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		jedisClusterNodes.add(new HostAndPort(HOST1, PORT1));
		jedisClusterNodes.add(new HostAndPort(HOST2, PORT2));
		jedisClusterNodes.add(new HostAndPort(HOST3, PORT3));
		
		if(!StringUtils.isEmpty(HOST4))
			jedisClusterNodes.add(new HostAndPort(HOST4, PORT4));
		
		if(!StringUtils.isEmpty(HOST5))
			jedisClusterNodes.add(new HostAndPort(HOST5, PORT5));
		
		if(!StringUtils.isEmpty(HOST6))
			jedisClusterNodes.add(new HostAndPort(HOST6, PORT6));
		JedisCluster jc = new JedisCluster(jedisClusterNodes, new JedisPoolConfig());
		return jc;
	}

}
