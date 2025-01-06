package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private static final String DEFAULT_USER_ROLE = "USER";
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        LOGGER.info("Entering getUserByEmail with email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            LOGGER.error("User not found: {}"   , email);
            return new UsernameNotFoundException("User not found");
        });
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


    public User getUserByid(Long id) {
        LOGGER.info("Entering getUserByid with id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("User not found with id: {}"   , id);
            return new UsernameNotFoundException("User not found");
        });
    }

    public Optional<UserDto> findCredentialsByEmail(String email) {
        LOGGER.info("Entering findCredentialsByEmail with email: {}", email);
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }


    public Long getUserIdByUsername(String username) {
        LOGGER.info("Entering getUserIdByUsername with username: {}", username);
        return userRepository.findByEmail(username).orElseThrow(() -> {
            LOGGER.error("User not found with username: {}", username);
            return new RuntimeException("User not found");
        }).getId();
    }

    public boolean usernameExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }


    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto) {
        LOGGER.info("Entering registerUserWithDefaultRole with userRegistrationDto: {}", userRegistrationDto);

        if (usernameExists(userRegistrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        System.out.println("1");
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setUserName(userRegistrationDto.getUsername());
        String passwordHash = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(passwordHash);
        System.out.println("2");

//        UserRole defaultRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
//        System.out.println(defaultRole.getName());
//        System.out.println("WTFFFFFF");
//        user.getRoles().add(defaultRole);
        System.out.println("3");
        userRepository.save(user);
        System.out.println("4");
        LOGGER.info("User registered successfully with default role");
    }

    @Transactional
    public void updateUser(UserDto userDto) {
        LOGGER.info("Entering updateUser with userDto: {}", userDto);

        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> {
                    LOGGER.error("User not found with email: {}", userDto.getEmail());
                    return new NoSuchElementException("User not found");
                });

        boolean updated = false;

        if(userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            String passwordHash = passwordEncoder.encode(userDto.getPassword());
            if(!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                user.setPassword(passwordHash);
                updated = true;
            }
        }

        if(userDto.getUserName() != null && !userDto.getUserName().isEmpty() && !userDto.getUserName().equals(user.getUserName())) {
            user.setUserName(userDto.getUserName());
            updated = true;
        }


        if(updated) {
            userRepository.save(user);
            LOGGER.info("User updated successfully");
        } else {
            LOGGER.info("No changes detected, user not updated");
        }
    }


    public boolean verifyCurrentPassword(String email, String currentPassword) {
        LOGGER.info("Entering verifyCurrentPassword with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.error("User not found with email: {}", email);
                    return new NoSuchElementException("User not found");
                });

        boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
        LOGGER.info("Password verification result: {}", matches);
        return matches;
    }
}