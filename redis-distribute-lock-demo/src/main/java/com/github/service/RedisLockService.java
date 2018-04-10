package com.github.service;

import com.github.model.User;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RedisLockService {

	private final Logger logger = LoggerFactory.getLogger(RedisLockService.class);

	@RedisLock
	public void update(User user){
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			logger.error("exp", e);
		}
	}
	
}
