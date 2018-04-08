package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure;

import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.DistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisDistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLockAspect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;


@Configuration
@ConditionalOnBean(StringRedisTemplate.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisDistributeLockAutoConfiguration {

	@Bean
	public DistributeLock redisDistributedLock() {
		return new RedisDistributeLock();
	}

	@Bean
	public RedisLockAspect redisLockAspect() {
		return new RedisLockAspect();
	}

}
