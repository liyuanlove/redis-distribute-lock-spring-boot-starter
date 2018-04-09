package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;


public interface DistributeLock {

	boolean lock(String key);
	
	boolean lock(String key, Integer retryTimes);
	
	boolean lock(String key, Integer retryTimes, Long sleepMillis);
	
	boolean lock(String key, Long expire);
	
	boolean lock(String key, Long expire, Integer retryTimes);
	
	boolean lock(String key, Long expire, Integer retryTimes, Long sleepMillis);
	
	boolean releaseLock(String key);
}
