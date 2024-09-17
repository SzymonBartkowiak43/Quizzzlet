package com.example.quizlecikprojekt.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
public class CustomSecurityConfig {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String LOGIN_PAGE = "/login";
    private static final String LOGOUT_URL = "/logout/**";
    private static final String LOGOUT_SUCCESS_URL = "/login?logout";
    private static final String[] PUBLIC_MATCHERS = {
            "/", "/registration","/home","/styles/**", "/img/**", "/scripts/**", "static/img/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        PathRequest.H2ConsoleRequestMatcher h2ConsoleRequestMatcher = PathRequest.toH2Console();

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/registration").permitAll()
                .requestMatchers(PUBLIC_MATCHERS).permitAll()
                .requestMatchers(h2ConsoleRequestMatcher).permitAll()
                .anyRequest().authenticated()
                )
        .formLogin(login -> login
                .loginPage(LOGIN_PAGE)
                .permitAll()
        )
        .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL, HttpMethod.GET.name()))
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL).permitAll()
        );

        http.csrf(csrf -> csrf.ignoringRequestMatchers(h2ConsoleRequestMatcher));
        http.headers(
                config -> config.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                )
        );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
