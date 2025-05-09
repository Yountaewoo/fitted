package com.fitted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.fitted")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}