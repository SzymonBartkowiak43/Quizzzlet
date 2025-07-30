package com.example.quizlecikprojekt;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@EnableScheduling
public class QuizlecikProjektApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizlecikProjektApplication.class, args);
    }

}