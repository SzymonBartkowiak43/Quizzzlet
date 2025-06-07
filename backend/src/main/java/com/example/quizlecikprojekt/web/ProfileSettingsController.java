package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserService;
import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProfileSettingsController {
    private final UserService userService;


    public ProfileSettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profileSettings")
    public String showProfileSettingsForm(Model model) {
        String email = getCurrentUserEmail();
        userService.findCredentialsByEmail(email).ifPresent(userDto -> model.addAttribute("user", userDto));
        return "profileSettings";
    }

    @PostMapping("/profileSettings")
    public String updateProfileSettings(@RequestParam String email,
                                        @RequestParam String userName,
                                        @RequestParam String currentPassword,
                                        @RequestParam Optional<String> newPassword,
                                        Model model) {


        if (userService.verifyCurrentPassword(email, currentPassword)) {
            UserDto userDto = new UserDto();
            userDto.setEmail(email);

            boolean updated = false;
            User currentUser = userService.getUserByEmail(email);

            if (!userName.isEmpty() && !userName.equals(currentUser.getUserName())) {
                userDto.setUserName(userName);
                updated = true;
            }

            if (newPassword.isPresent() && !newPassword.get().equals(currentPassword)) {
                userDto.setPassword(newPassword.get());
                updated = true;
            }


            if (updated) {
                userService.updateUser(userDto);
                return "redirect:/profileSettings?success";
            } else {
                model.addAttribute("error", "No changes detected.");
                return "profileSettings";
            }

        } else {
            model.addAttribute("error", "Current password is incorrect.");
            return "error";
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
