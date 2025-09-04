package com.rulesengine.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

/**
 * The main entry point for the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = "com.rulesengine") // Tells Spring to scan all our modules
@EnableMongoRepositories(basePackages = "com.rulesengine.app.repository") // Specifies where to find our database repositories
public class RulesEngineApplication {

    /**
     * The main method that starts the entire application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RulesEngineApplication.class, args);
    }

    /**
     * Creates a bean for RestTemplate. This is a standard Spring utility
     * for making HTTP requests to other services, which we'll use to call
     * our Python NLP service.
     *
     * @return A RestTemplate instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

