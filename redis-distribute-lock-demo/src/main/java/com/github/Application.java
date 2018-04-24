package com.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {

		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		// 核心线程数
		threadPoolTaskExecutor.setCorePoolSize(8);
		// 最大线程数
		threadPoolTaskExecutor.setMaxPoolSize(32);
		// 运行线程满时，等待队列的大小
		threadPoolTaskExecutor.setQueueCapacity(64);
		// 池和队列满的策略, 调用者的线程会执行该任务, 如果执行器已关闭, 则丢弃
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 是否允许释放核心线程
		threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
		return threadPoolTaskExecutor;
	}
}

