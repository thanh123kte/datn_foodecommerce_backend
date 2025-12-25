package com.example.qtifood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QtifoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(QtifoodApplication.class, args);
	}

}
