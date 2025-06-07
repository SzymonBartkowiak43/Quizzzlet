package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.PasswordValidator;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
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

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        return "registration-form";
    }

    @PostMapping("/registration")
    public String register(UserRegistrationDto userRegistrationDto, Model model) {

        List<String> constraintViolations = PasswordValidator.getConstraintViolations(userRegistrationDto.getPassword());
        if (!constraintViolations.isEmpty()) {
            model.addAttribute("constraintViolations", constraintViolations);
            model.addAttribute("user", userRegistrationDto);
            return "registration-form";
        }

        if (userService.emailExists(userRegistrationDto.getEmail())) {
            model.addAttribute("constraintViolations", List.of("Email already exists"));
            model.addAttribute("user", userRegistrationDto);
            return "registration-form";
        }
        if (userService.usernameExists(userRegistrationDto.getUsername())) {
            model.addAttribute("constraintViolations", List.of("Username already exists"));
            model.addAttribute("user", userRegistrationDto);
            return "registration-form";
        }

        try {
            userService.registerUserWithDefaultRole(userRegistrationDto);
        } catch (Exception e) {
            return "registration-form";
        }
        return "redirect:/login";
    }
}

