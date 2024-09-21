package com.example.quizlecikprojekt.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String loginForm() {
        LOGGER.info("Entering loginForm");
        return "login-form";
    }

//    @PostMapping("/login")
//    public String login() {
//        LOGGER.info("Entering login");
//        return "redirect:/";
//    }

}
