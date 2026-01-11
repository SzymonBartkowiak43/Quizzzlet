package pl.bartkowiak.quizlecikprojekt.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "auth.jwt")
public record JwtConfigurationProperties(String secret, long expirationDays, String issuer) {}
