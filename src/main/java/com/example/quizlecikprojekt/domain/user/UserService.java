package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            return new UsernameNotFoundException("User not found");
        });
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            return new UsernameNotFoundException("User not found");
        });
    }

    public Optional<UserDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }


    public Long getUserIdByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> {
            return new RuntimeException("User not found");
        }).getId();
    }

    public boolean usernameExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }


    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto) {

        if (usernameExists(userRegistrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setUserName(userRegistrationDto.getUsername());
        String passwordHash = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(passwordHash);

//        UserRole defaultRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
//        System.out.println(defaultRole.getName());
//        System.out.println("WTFFFFFF");
//        user.getRoles().add(defaultRole);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UserDto userDto) {

        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> {
                    return new NoSuchElementException("User not found");
                });

        boolean updated = false;

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            String passwordHash = passwordEncoder.encode(userDto.getPassword());
            if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                user.setPassword(passwordHash);
                updated = true;
            }
        }

        if (userDto.getUserName() != null && !userDto.getUserName().isEmpty() && !userDto.getUserName().equals(user.getUserName())) {
            user.setUserName(userDto.getUserName());
            updated = true;
        }


        if (updated) {
            userRepository.save(user);
        } else {
            System.out.println("User already exists!!!");
        }
    }


    public boolean verifyCurrentPassword(String email, String currentPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new NoSuchElementException("User not found");
                });

        return passwordEncoder.matches(currentPassword, user.getPassword());
    }
}