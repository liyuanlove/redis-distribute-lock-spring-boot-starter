package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;

public abstract class AbstractDistributeLock implements DistributeLock {

	/** 未获取到锁时, 线程休眠时间 */
	public static final Long SLEEP_MILLIS = 500L;
	/** 锁的超时时间 */
	public static final Long EXPIRE_MILLIS = 30 * 1000L;
	/** MAX_WAITE =  RETRY_TIMES * SLEEP_MILLIS = 10seconds */
	public static final Integer RETRY_TIMES = 20;

	@Override
	public boolean lock(String key) {
		return lock(key, RETRY_TIMES);
	}

	@Override
	public boolean lock(String key, Integer retryTimes) {
		return lock(key, retryTimes, SLEEP_MILLIS);
	}

	@Override
	public boolean lock(String key, Integer retryTimes, Long sleepMillis) {
		return lock(key, EXPIRE_MILLIS, retryTimes, sleepMillis);
	}

	@Override
	public boolean lock(String key, Long expire) {
		return lock(key, expire, RETRY_TIMES);
	}

	@Override
	public boolean lock(String key, Long expire, Integer retryTimes) {
		return lock(key, expire, retryTimes, SLEEP_MILLIS);
	}

}
