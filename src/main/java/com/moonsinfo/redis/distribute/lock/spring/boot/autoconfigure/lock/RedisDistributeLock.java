package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedisDistributeLock extends AbstractDistributeLock {
	
	@Resource private StringRedisTemplate stringRedisTemplate;
	/* 保存当前调用线程的Redis setnx value */
	private ThreadLocal<String> redisThreadValue = new ThreadLocal<String>();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String UNLOCK_LUA;

    static {
        StringBuffer stringBuffer = new StringBuffer();
	    stringBuffer.append("if redis.call(\"get\", KEYS[1]) == ARGV[1] then ");
	    stringBuffer.append("    return redis.call(\"del\", KEYS[1]) ");
	    stringBuffer.append("else ");
	    stringBuffer.append("    return 0 ");
	    stringBuffer.append("end");
        UNLOCK_LUA = stringBuffer.toString();
    }

	@Override
	public boolean lock(String key, Long expire, Integer retryTimes, Long sleepMillis) {

		boolean result = setRedis(key, expire);

		// 如果获取锁失败，按照传入的重试次数进行重试
		while(!result && retryTimes-- > 0) {
			try {
				logger.debug("lock failed, retrying..." + retryTimes);
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				logger.warn("lock occur an exception.", e);
				return false;
			}
			result = setRedis(key, expire);
		}
		return result;
	}
	
	private boolean setRedis(String key, Long expire) {
		try {
			String result = stringRedisTemplate.execute(new RedisCallback<String>() {
				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					JedisCommands commands = (JedisCommands) connection.getNativeConnection();
					String uuid = UUID.randomUUID().toString();
					redisThreadValue.set(uuid);
					return commands.set(key, uuid, "NX", "PX", expire);
				}
			});
			return !StringUtils.isEmpty(result);
		} catch (Exception e) {
			logger.error("set redis occur an exception", e);
		}
		return false;
	}
	
	@Override
	public boolean releaseLock(String key) {

		// 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
		try {
			List<String> keys = new ArrayList<String>();
			keys.add(key);

			List<String> args = new ArrayList<String>();
			args.add(redisThreadValue.get());

			// 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
			// spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
			
			Long result = stringRedisTemplate.execute(new RedisCallback<Long>() {
				public Long doInRedis(RedisConnection connection) throws DataAccessException {

					// 移除redisThreadValue, 防止内存溢出, 无法GC问题
					redisThreadValue.remove();

					Object nativeConnection = connection.getNativeConnection();
					// 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
					if (nativeConnection instanceof JedisCluster) {
						// 集群模式
						return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
					} else if (nativeConnection instanceof Jedis) {
						// 单机模式
						return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
					}
					return 0L;
				}
			});

			return result != null && result > 0;
		} catch (Exception e) {
			logger.error("release lock occur an exception", e);
		}
		return false;
	}
	
}
