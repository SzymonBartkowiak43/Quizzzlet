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

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        LOGGER.info("Entering registrationForm");
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        return "registration-form";
    }

    @PostMapping("/registration")
    public String register(UserRegistrationDto userRegistrationDto) {
        LOGGER.info("Entering register with user: {}", userRegistrationDto.getEmail());
        try {
            userService.registerUserWithDefaultRole(userRegistrationDto);
        } catch (Exception e) {
            LOGGER.error("Error during registration: {}", e.getMessage());
            return "redirect:/registration?error";
        }
        return "redirect:/";
    }
}

