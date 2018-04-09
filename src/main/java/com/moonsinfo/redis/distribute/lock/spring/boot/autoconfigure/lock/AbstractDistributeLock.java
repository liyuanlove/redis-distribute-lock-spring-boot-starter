package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;

public abstract class AbstractDistributeLock implements DistributeLock {

	public static final Long TIMEOUT_MILLIS = 30 * 1000L;
	public static final Integer RETRY_TIMES = Integer.MAX_VALUE;
	public static final Long SLEEP_MILLIS = 500L;

	@Override
	public boolean lock(String key) {
		return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS);
	}

	@Override
	public boolean lock(String key, Integer retryTimes) {
		return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS);
	}

	@Override
	public boolean lock(String key, Integer retryTimes, Long sleepMillis) {
		return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis);
	}

	@Override
	public boolean lock(String key, Long expire) {
		return lock(key, expire, RETRY_TIMES, SLEEP_MILLIS);
	}

	@Override
	public boolean lock(String key, Long expire, Integer retryTimes) {
		return lock(key, expire, retryTimes, SLEEP_MILLIS);
	}

}
