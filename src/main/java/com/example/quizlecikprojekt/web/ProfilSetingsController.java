package com.example.quizlecikprojekt.web;

import com.example.quizlecikprojekt.domain.user.Dto.UserDto;
import com.example.quizlecikprojekt.domain.user.UserService;
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

    public ProfilSetingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profileSettings")
    public String showProfileSettingsForm(Model model) {
        String email = getCurrentUserEmail();
        userService.findCredentialsByEmail(email).ifPresent(userDto -> model.addAttribute("user", userDto));
        return "profileSettings";
    }

    @PostMapping("/profileSettings")
    public String updateProfileSettings(@RequestParam String email,@RequestParam String userName  ,@RequestParam String currentPassword, @RequestParam String newPassword, Model model) {
        if (userService.verifyCurrentPassword(email, currentPassword)) {
            UserDto userDto = new UserDto();
            if(!email.isEmpty()) {
                userDto.setEmail(email);
            }
            if(!userName.isEmpty()) {
                userDto.setUserName(userName);
            }
            userDto.setPassword(newPassword);
            userService.updateUser(userDto);
            return "redirect:/profileSettings?success";
        } else {
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
