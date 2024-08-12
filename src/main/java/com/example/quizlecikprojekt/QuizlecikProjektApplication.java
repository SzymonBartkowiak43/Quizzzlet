package com.example.quizlecikprojekt;

import com.example.quizlecikprojekt.web.RegistrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizlecikProjektApplication {
    private final static Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);
    public static void main(String[] args) {
        LOGGER.info("Starting QuizlecikProjektApplication");
        SpringApplication.run(QuizlecikProjektApplication.class, args);
    }

}
