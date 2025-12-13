package com.example.securecustomerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SecureCustomerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureCustomerApiApplication.class, args);
	}

}
