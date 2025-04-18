package com.example.enrolment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnrolmentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnrolmentServiceApplication.class, args);
        System.out.println("Enrolment service up and running");
    }
}
