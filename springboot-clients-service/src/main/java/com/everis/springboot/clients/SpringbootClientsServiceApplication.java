package com.everis.springboot.clients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringbootClientsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootClientsServiceApplication.class, args);
	}

}
