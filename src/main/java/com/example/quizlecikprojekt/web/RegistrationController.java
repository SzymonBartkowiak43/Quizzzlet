package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.PasswordValidator;
import com.example.quizlecikprojekt.domain.user.Dto.UserRegistrationDto;
import com.example.quizlecikprojekt.domain.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
    public String register(UserRegistrationDto userRegistrationDto, Model model) {
        LOGGER.info("Entering register with user: {}", userRegistrationDto.getEmail());

        List<String> constraintViolations = PasswordValidator.getConstraintViolations(userRegistrationDto.getPassword());
        if (!constraintViolations.isEmpty()) {
            LOGGER.error("Password validation failed: {}", constraintViolations);
            model.addAttribute("constraintViolations", constraintViolations);
            model.addAttribute("user", userRegistrationDto);
            return "redirect:/registration?error";
        }

        if (userService.emailExists(userRegistrationDto.getEmail())) {
            LOGGER.error("User already exists: {}", userRegistrationDto.getEmail());
            model.addAttribute("constraintViolations", List.of("Email already exists"));
            model.addAttribute("user", userRegistrationDto);
            return "redirect:/registration?error";
        }
        if (userService.usernameExists(userRegistrationDto.getUsername())) {
            LOGGER.error("Username already exists: {}", userRegistrationDto.getUsername());
            model.addAttribute("constraintViolations", List.of("Username already exists"));
            model.addAttribute("user", userRegistrationDto);
            return "redirect:/registration?error";
        }

        try {
            LOGGER.error("Registering user: {}", userRegistrationDto.getEmail());
            LOGGER.error("Registering user: {}", userRegistrationDto.getUsername());
            userService.registerUserWithDefaultRole(userRegistrationDto);
        } catch (Exception e) {
            LOGGER.error("Error during registration: {}", e.getMessage());
            return "redirect:/registration?error";
        }
        return "redirect:/";
    }
}

