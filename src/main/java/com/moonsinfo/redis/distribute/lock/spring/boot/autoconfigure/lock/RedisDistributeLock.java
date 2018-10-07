package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RedisDistributeLock extends AbstractDistributeLock implements InitializingBean {
	
	@Resource private StringRedisTemplate stringRedisTemplate;
	/* 保存当前调用线程的Redis setnx value */
	private ThreadLocal<String> redisThreadValue = new ThreadLocal<>();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static RedisScript<Long> redisScript = null;

	@Override
	public void afterPropertiesSet() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("if redis.call(\"get\", KEYS[1]) == ARGV[1] then ");
		stringBuffer.append("    return redis.call(\"del\", KEYS[1]) ");
		stringBuffer.append("else ");
		stringBuffer.append("    return 0 ");
		stringBuffer.append("end");

		String unlockLua = stringBuffer.toString();
		redisScript = new DefaultRedisScript<>(unlockLua, Long.class);
	}


	@Override
	public boolean lock(String key, Long expire, Integer retryTimes, Long sleepMillis) {

		Boolean result = setRedis(key, expire);

		// 如果获取锁失败，按照传入的重试次数进行重试
		while(!result && retryTimes-- > 0) {
			try {
				logger.debug("lock fail, retrying: " + retryTimes);
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				logger.warn("lock occur an exception.", e);
				return false;
			}
			result = setRedis(key, expire);
		}
		return result;
	}
	
	private Boolean setRedis(String key, Long expire) {
		return stringRedisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				String uuid = UUID.randomUUID().toString();
				redisThreadValue.set(uuid);
				return connection.set(key.getBytes(), uuid.getBytes(), Expiration.milliseconds(expire), RedisStringCommands.SetOption.SET_IF_ABSENT);
			}
		});
	}
	
	@Override
	public boolean releaseLock(String key) {

		// 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
		try {

			// 使用lua脚本删除redis中匹配value的key，可以避免方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
			Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), redisThreadValue.get());

			// 移除redisThreadValue, 防止内存溢出, 无法GC问题
			redisThreadValue.remove();

			return result != null && result > 0;
		} catch (Exception e) {
			logger.error("release lock occur an exception", e);
		}
		return false;
	}
	
}
