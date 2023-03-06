package com.rstejskalprojects.reservationsystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ReservationsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsystemApplication.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("Application started!");
	}


}
