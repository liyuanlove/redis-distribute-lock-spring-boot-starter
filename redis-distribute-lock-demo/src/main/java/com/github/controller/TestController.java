package com.github.controller;

import com.github.model.User;
import com.github.service.RedisLockService;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.DistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisDistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.management.relation.RelationService;


@Controller
@RequestMapping("t")
public class TestController {

	@Resource private DistributeLock distributeLock;
	@Resource private RedisLockService redisLockService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ResponseBody
	@GetMapping({"", "/"})
	public Object index(@RequestParam(defaultValue = "100") Integer id, @RequestParam(defaultValue = "iMiracle") String name) {

		for(int i = 0; i < 5; i++){
			new RedisLockThread().start();
		}

		return "success";
	}

	@ResponseBody
	@GetMapping("aspect")
	public Object index() {

		for(int i = 0; i < 50; i++){
			new RedisLockAspectThread().start();
		}


		return "success";
	}


	class RedisLockThread extends Thread {
		@Override
		public void run() {
			String key = "lockKey";
			boolean result = distributeLock.lock(key);
			result = distributeLock.lock(key);
			result = distributeLock.lock(key);
			result = distributeLock.lock(key);
			result = distributeLock.lock(key);
			result = distributeLock.lock(key);


			logger.info(result ? "get lock success : " + key : "get lock failed : " + key);
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				logger.error("exp", e);
			} finally {
				distributeLock.releaseLock(key);
				logger.info("release lock : " + key);
			}
		}
	}

	class RedisLockAspectThread extends Thread {
		@Override
		public void run() {
			User user = new User();
			user.setId(1);
			user.setName("iMiracle");
			redisLockService.update(user);
		}
	}

}
