package org.emotion.detect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the emotion detection backend
 * This is where the Spring Boot application starts
 */
@SpringBootApplication
public class App {
    /**
     * Main method to start the Spring Boot application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}