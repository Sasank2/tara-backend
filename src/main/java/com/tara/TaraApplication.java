package com.tara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaraApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaraApplication.class, args);
    }
}
