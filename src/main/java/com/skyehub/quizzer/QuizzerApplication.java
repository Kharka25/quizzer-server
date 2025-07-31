package com.skyehub.quizzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class QuizzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizzerApplication.class, args);
	}

}
