package com.github.controller;

import com.moonsinfo.redis.distribute.lock.spring.boot.autoconfigure.lock.RedisLock;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("t")
public class TestController {


	@RedisLock
	@ResponseBody
	@GetMapping({"", "/"})
	public Object index(@RequestParam(defaultValue = "100") Integer id, @RequestParam(defaultValue = "iMiracle") String name) {

		return "success";
	}

	@ResponseBody
	@GetMapping("page")
	public Object page() {

		return "success";
	}

}
