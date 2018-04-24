package com.github.service;

import com.github.model.User;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RedisLockService {

	private final Logger logger = LoggerFactory.getLogger(RedisLockService.class);
	private int counter = 0;

	public void setCounter(int counter) {
		this.counter = counter;
	}

	@RedisLock(key = "'redis:lock:user:id:'.concat(#user.id)", sleepMills = 10L, retryTimes = 100)
	public void update(User user){
		try {
			counter++;
			System.err.println(counter);

//			Thread.sleep(2);
		} catch (Exception e) {
			logger.error("exp", e);
		}
	}
	
}
