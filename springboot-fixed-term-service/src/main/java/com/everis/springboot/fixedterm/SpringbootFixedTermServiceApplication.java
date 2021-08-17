package com.everis.springboot.fixedterm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringbootFixedTermServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootFixedTermServiceApplication.class, args);
	}

}
