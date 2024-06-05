package com.example.quizlecikprojekt.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    Optional<Users> getUsersById(Long id) {
        return usersRepository.findById(id);
    }

    Users saveUsers(Users user) {
        return usersRepository.save(user);
    }

    Optional<Users> updateUsers(Long userId,Users user) {
        if(!usersRepository.existsById(userId)) {
            return Optional.empty();
        }
        user.setId(userId);
        return Optional.of(usersRepository.save(user));
    }
}
