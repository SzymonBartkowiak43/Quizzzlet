package com.example.quizlecikprojekt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizlecikProjektApplication {
    private final static Logger LOGGER = LoggerFactory.getLogger(QuizlecikProjektApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(QuizlecikProjektApplication.class, args);
        LOGGER.info("QuizlecikProjektApplication started");

    }

}
