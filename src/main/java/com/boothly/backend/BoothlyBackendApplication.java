package com.boothly.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoothlyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoothlyBackendApplication.class, args);
    }

}
