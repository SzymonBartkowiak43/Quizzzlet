package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.Dto.UserRegistrationDto;
import com.example.quizlecikprojekt.domain.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;
    private final static Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/rejestracja")
    public String registrationForm(Model model) {
        LOGGER.info("Entering registration form");
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        LOGGER.info("Exiting registration form");
        return "registration-form";
    }

    @PostMapping("/rejestracja")
    public String register(UserRegistrationDto userRegistrationDto) {
        LOGGER.info("Entering register method with userRegistrationDto: {}", userRegistrationDto);
        userService.registerUserWithDefaultRole(userRegistrationDto);
        LOGGER.info("User registered successfully with email: {}", userRegistrationDto.getEmail());
        LOGGER.info("Exiting register method");
        return "redirect:/";
    }
}

