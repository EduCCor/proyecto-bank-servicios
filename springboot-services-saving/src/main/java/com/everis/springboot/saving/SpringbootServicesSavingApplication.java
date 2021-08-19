package com.everis.springboot.saving;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringbootServicesSavingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicesSavingApplication.class, args);
	}

}
