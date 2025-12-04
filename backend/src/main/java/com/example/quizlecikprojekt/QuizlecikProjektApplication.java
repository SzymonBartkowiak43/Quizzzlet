package com.example.quizlecikprojekt;

import com.example.quizlecikprojekt.config.security.JwtConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@EnableConfigurationProperties(value = JwtConfigurationProperties.class)
public class QuizlecikProjektApplication {

  public static void main(String[] args) {
    SpringApplication.run(QuizlecikProjektApplication.class, args);
  }
}
