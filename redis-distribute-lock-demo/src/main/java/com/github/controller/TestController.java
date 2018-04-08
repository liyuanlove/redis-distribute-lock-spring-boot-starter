package com.github.controller;

import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.DistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisDistributeLock;
import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


@Controller
@RequestMapping("t")
public class TestController {

	@Resource private DistributeLock distributeLock;

	@RedisLock
	@ResponseBody
	@GetMapping({"", "/"})
	public Object index(@RequestParam(defaultValue = "100") Integer id, @RequestParam(defaultValue = "iMiracle") String name) {

		System.err.println(distributeLock);
		return "success";
	}

	@ResponseBody
	@GetMapping("page")
	public Object page() {

		return "success";
	}

}
