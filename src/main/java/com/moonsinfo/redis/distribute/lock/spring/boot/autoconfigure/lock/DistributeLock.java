package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;

public interface DistributeLock {

	boolean lock(String key);
	
	boolean lock(String key, int retryTimes);
	
	boolean lock(String key, int retryTimes, long sleepMillis);
	
	boolean lock(String key, long expire);
	
	boolean lock(String key, long expire, int retryTimes);
	
	boolean lock(String key, long expire, int retryTimes, long sleepMillis);
	
	boolean releaseLock(String key);
}
