package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.Dto.UserDto;
import com.example.quizlecikprojekt.domain.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfilSetingsController {
    private final UserService userService;
    private final static Logger LOGGER = LoggerFactory.getLogger(ProfilSetingsController.class);


    public ProfilSetingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profileSettings")
    public String showProfileSettingsForm(Model model) {
        LOGGER.info("Entering showProfileSettingsForm");
        String email = getCurrentUserEmail();
        userService.findCredentialsByEmail(email).ifPresent(userDto -> model.addAttribute("user", userDto));
        LOGGER.info("Returning profileSettings form for email: {}", email);
        return "profileSettings";
    }

    @PostMapping("/profileSettings")
    public String updateProfileSettings(@RequestParam String email,@RequestParam String userName  ,@RequestParam String currentPassword, @RequestParam String newPassword, Model model) {
        LOGGER.info("Entering updateProfileSettings with email: {}", email);
        if (userService.verifyCurrentPassword(email, currentPassword)) {
            UserDto userDto = new UserDto();
            if (!email.isEmpty()) {
                userDto.setEmail(email);
            }
            if (!userName.isEmpty()) {
                userDto.setUserName(userName);
            }
            userDto.setPassword(newPassword);
            userService.updateUser(userDto);
            LOGGER.info("Profile updated for email: {}", email);
            return "redirect:/profileSettings?success";
        } else {
            LOGGER.warn("Current password is incorrect for email: {}", email);
            model.addAttribute("error", "Current password is incorrect.");
            return "profileSettings";
        }
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
