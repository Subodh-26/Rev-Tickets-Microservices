package com.revature.revtickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableMongoAuditing
@EnableScheduling
public class RevTicketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevTicketsApplication.class, args);
    }
}
