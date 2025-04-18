package com.example.module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ModuleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleServiceApplication.class, args);
        System.out.println("Module service up and running");
    }
}
