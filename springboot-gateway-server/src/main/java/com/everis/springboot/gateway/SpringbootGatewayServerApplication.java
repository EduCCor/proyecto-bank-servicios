package com.everis.springboot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringbootGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootGatewayServerApplication.class, args);
	}

}
