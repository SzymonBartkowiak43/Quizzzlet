package com.example.quizlecikprojekt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
public class CustomSecurityConfig {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/").permitAll()
                .requestMatchers("/rejestracja").permitAll()
                .requestMatchers("/log").hasAnyRole(USER_ROLE, ADMIN_ROLE))
        .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
        )
        .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                .logoutSuccessUrl("/login?logout").permitAll()
        );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/styles/**"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
