package com.example.quizlecikprojekt;


import com.example.quizlecikprojekt.config.LogTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class QuizlecikProjektApplication {
    private final static Logger LOGGER = LoggerFactory.getLogger(QuizlecikProjektApplication.class);
    private final LogTransferService logTransferService = new LogTransferService();

    public static void main(String[] args) {
        SpringApplication.run(QuizlecikProjektApplication.class, args);
        LOGGER.info("QuizlecikProjektApplication started");
    }

    @Scheduled(fixedRate = 300000)
    public void transferLogs() {
        logTransferService.transferLogs();
    }
}