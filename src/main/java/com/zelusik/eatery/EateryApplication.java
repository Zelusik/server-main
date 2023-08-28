package com.zelusik.eatery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/env.properties")
@SpringBootApplication
public class EateryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EateryApplication.class, args);
    }

}
