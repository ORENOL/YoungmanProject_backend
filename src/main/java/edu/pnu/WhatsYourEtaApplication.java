package edu.pnu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class WhatsYourEtaApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhatsYourEtaApplication.class, args);
	}

}
