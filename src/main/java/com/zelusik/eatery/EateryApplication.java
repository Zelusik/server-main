package com.zelusik.eatery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EateryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EateryApplication.class, args);
    }

}
