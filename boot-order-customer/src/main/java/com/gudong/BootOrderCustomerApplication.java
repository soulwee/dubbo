package com.gudong;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@EnableHystrix
@EnableDubbo
@SpringBootApplication
public class BootOrderCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootOrderCustomerApplication.class, args);
	}

}
