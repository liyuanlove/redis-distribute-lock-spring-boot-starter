package com.github.controller;

import com.github.model.User;
import com.github.service.RedisLockService;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.DistributeLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


@Controller
@RequestMapping
public class IndexController {

	@Resource private DistributeLock distributeLock;
	@Resource private RedisLockService redisLockService;
	@Resource private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ResponseBody
	@GetMapping({"", "/"})
	public Object index(@RequestParam(defaultValue = "100") Integer id, @RequestParam(defaultValue = "iMiracle") String name) {

		for(int i = 0; i < 50; i++){
			new RedisLockThread().start();
		}

		return "success";
	}

	@ResponseBody
	@GetMapping("t")
	public Object t(@RequestParam(defaultValue = "100") Integer id, @RequestParam(defaultValue = "iMiracle") String name) {

		redisLockService.update(new User(id, name));

		return "success";
	}

	@ResponseBody
	@GetMapping("aspect")
	public Object index() {

		redisLockService.setCounter(0);
		for(int i = 0; i < 5000; i++){
//			new RedisLockAspectThread().start();
			threadPoolTaskExecutor.execute(new RedisLockAspectThread());
		}


		return "success";
	}


	class RedisLockThread extends Thread {
		@Override
		public void run() {
			String key = "redis:lock:key";
			boolean result = distributeLock.lock(key);
			logger.info(result ? "get lock success : " + key : "get lock failed : " + key);

			try {
				Thread.sleep(20);
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
