package com.everis.springboot.createaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringbootCreateAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootCreateAccountServiceApplication.class, args);
	}

}
