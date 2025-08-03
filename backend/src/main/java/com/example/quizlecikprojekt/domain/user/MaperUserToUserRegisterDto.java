package com.example.quizlecikprojekt.domain.user;


import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import org.springframework.stereotype.Component;

@Component
class MaperUserToUserRegisterDto {

    UserRegisterDto map(User user) {
        if(user == null) {
            return null;
        }

        return new UserRegisterDto(
                user.getEmail(),
                user.getName(),
                user.getPassword()

        );
    }
}
