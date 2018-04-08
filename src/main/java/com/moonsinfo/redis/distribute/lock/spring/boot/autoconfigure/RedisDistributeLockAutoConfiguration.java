package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure;

import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.DistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisDistributeLock;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.UnknownHostException;


@Configuration
@ConditionalOnClass(DistributeLock.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ImportAutoConfiguration({RedisDistributeLockAspectConfiguration.class})
public class RedisDistributeLockAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
		return stringRedisTemplate;
	}

	@Bean
	@ConditionalOnBean(StringRedisTemplate.class)
	public DistributeLock redisDistributedLock() {
		return new RedisDistributeLock();
	}

}
