package com.rafaelaribeiro.sistema_flashcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SistemaFlashcardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaFlashcardsApplication.class, args);
	}

}
