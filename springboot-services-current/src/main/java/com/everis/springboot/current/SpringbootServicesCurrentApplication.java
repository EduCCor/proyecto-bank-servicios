package com.everis.springboot.current;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringbootServicesCurrentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicesCurrentApplication.class, args);
	}

}
