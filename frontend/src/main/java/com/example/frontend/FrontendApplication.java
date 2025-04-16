package com.example.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class FrontendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
        System.out.println("Frontend up and running");
    }

    @Bean
    public CommandLineRunner testUserServiceConnection(@Value("${user.service.url}") String userServiceUrl) {
        return args -> {
            RestTemplate restTemplate = new RestTemplate();
            String healthCheckUrl = userServiceUrl + "/api/health";

            int attempts = 0;
            int maxAttempts = 10;

            while (attempts < maxAttempts) {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);
                    System.out.println("User service reachable! Response code: " + response.getStatusCode());
                    return;
                } catch (Exception e) {
                    attempts++;
                    System.out.println("Waiting for user-service (" + attempts + "/" + maxAttempts + ")");
                    Thread.sleep(2000);
                }
            }

            System.out.println("Could not reach user-service after retries.");
        };
    }
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
