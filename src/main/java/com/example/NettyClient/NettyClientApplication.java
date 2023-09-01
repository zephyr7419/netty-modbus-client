package com.example.NettyClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NettyClientApplication  {

	public static void main(String[] args) {
		SpringApplication.run(NettyClientApplication.class, args);
	}


}
