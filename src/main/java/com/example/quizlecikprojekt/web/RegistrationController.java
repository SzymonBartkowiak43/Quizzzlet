package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.Dto.UserRegistrationDto;
import com.example.quizlecikprojekt.domain.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/rejestracja")
    public String registrationForm(Model model) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        model.addAttribute("user", userRegistrationDto);
        return "registration-form";
    }

    @PostMapping("/rejestracja")
    public String register(UserRegistrationDto userRegistrationDto) {
        userService.registerUserWithDefaultRole(userRegistrationDto);
        return "redirect:/";
    }
}

