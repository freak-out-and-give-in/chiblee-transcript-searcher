package com.scrape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.scrape")
@EnableJpaRepositories({"com.scrape.repository"})
@EntityScan({"com.scrape.model"})
public class TranscriptVideosApplication {

	public static void main(String[] args) {
		SpringApplication.run(TranscriptVideosApplication.class, args);
	}

}
