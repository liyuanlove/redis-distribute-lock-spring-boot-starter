package com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure;

import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.DistributeLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.Resource;
import java.lang.reflect.Method;


@Aspect
@Configuration
@ConditionalOnClass(DistributeLock.class)
@AutoConfigureAfter(RedisDistributeLockAutoConfiguration.class)
public class RedisDistributeLockAspectConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource private DistributeLock distributeLock;
	
	private ExpressionParser parser = new SpelExpressionParser();
	private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

	@Pointcut("@annotation(com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock)")
	private void lockPoint(){
	}
	
	@Around("lockPoint()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable{

		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		RedisLock lockAction = method.getAnnotation(RedisLock.class);
		String key = lockAction.value();
		Object[] args = pjp.getArgs();
		key = parse(key, method, args);
		
		
		int retryTimes = lockAction.action().equals(RedisLock.LockFailAction.CONTINUE) ? lockAction.retryTimes() : 0;
		boolean lock = distributeLock.lock(key, lockAction.keepMills(), retryTimes, lockAction.sleepMills());
		if(!lock) {
			logger.debug("get lock failed : " + key);
			return null;
		}
		
		//得到锁,执行方法，释放锁
		logger.debug("get lock success : " + key);
		try {
			return pjp.proceed();
		} catch (Exception e) {
			logger.error("execute locked method occured an exception", e);
		} finally {
//			boolean releaseResult = distributeLock.releaseLock(key);
			boolean releaseResult = false;
			logger.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
		}
		return null;
	}
	
	/**
	 * @description 解析spring EL表达式
	 * @author fuwei.deng
	 * @date 2018年1月9日 上午10:41:01
	 * @version 1.0.0
	 * @param key 表达式
	 * @param method 方法
	 * @param args 方法参数
	 * @return
	 */
	private String parse(String key, Method method, Object[] args) {
		String[] params = discoverer.getParameterNames(method);
		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < params.length; i ++) {
			context.setVariable(params[i], args[i]);
		}
		return parser.parseExpression(key).getValue(context, String.class);
	}
}
