package org.ludus.ft7bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class Ft7botApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ft7botApplication.class, args);
	}

}
