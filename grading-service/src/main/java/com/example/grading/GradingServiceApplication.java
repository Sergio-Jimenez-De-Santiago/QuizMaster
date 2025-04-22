package com.example.grading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GradingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GradingServiceApplication.class, args);
        System.out.println("Grading service up and running");
    }
}
