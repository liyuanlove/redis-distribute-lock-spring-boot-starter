package com.github.service;

import com.github.model.User;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RedisLockService {

	private final Logger logger = LoggerFactory.getLogger(RedisLockService.class);
	private int counter = 0;

	public void setCounter(int counter) {
		this.counter = counter;
	}

	@RedisLock(key = "'redis:lock:user:id:'.concat(#user.id)", sleepMills = 10L, retryTimes = 1000)
	public void update(User user){
		try {
			counter++;
			System.err.println(counter);
		} catch (Exception e) {
			logger.error("exp", e);
		}
	}

	@RedisLock
	public void update1(User user){
		System.err.println("update user: " + user);
	}

}
