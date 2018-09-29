package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisLock {

	/** 锁的资源key, 支持spring El表达式*/
	@AliasFor("value")
	String key() default "'redis:lock:default'";

	@AliasFor("key")
	String value() default "'redis:lock:default'";

	/** 锁的超时时间, 默认30s, 根据执行时间按需调整 */
	long keepMills() default 30 * 1000L;
	
	/** 重试的间隔时间 */
    long sleepMills() default 100L;
    
    /** 重试次数*/
    int retryTimes() default 50;
}
