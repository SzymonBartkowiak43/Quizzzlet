package com.example.quizlecikprojekt.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        PathRequest.H2ConsoleRequestMatcher h2ConsoleRequestMatcher = PathRequest.toH2Console();

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/admin/**").hasAnyRole(ADMIN_ROLE)
                .requestMatchers("/zalogowany").hasAnyRole(ADMIN_ROLE, USER_ROLE)
                .anyRequest().permitAll()
        )
        .formLogin(withDefaults())
        .logout(logout -> logout
                 .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                 .logoutSuccessUrl("/").permitAll()
    );
        http.csrf(csrf -> csrf.ignoringRequestMatchers(h2ConsoleRequestMatcher));
        http.headers(
                config -> config.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                )
        );
        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//
//    }
}
