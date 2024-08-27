package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.Dto.UserDto;
import com.example.quizlecikprojekt.domain.user.Dto.UserRegistrationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto) {
        LOGGER.info("Entering registerUserWithDefaultRole with userRegistrationDto: {}", userRegistrationDto);

        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setUserName(userRegistrationDto.getUsername());
        String passwordHash = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(passwordHash);

        UserRole defaultRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
        user.getRoles().add(defaultRole);
        userRepository.save(user);
        LOGGER.info("User registered successfully with default role");
    }

    @Transactional
    public void updateUser(UserDto userDto) {
        LOGGER.info("Entering updateUser with userDto: {}", userDto);
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        LOGGER.info("User updated successfully");
    }

    public boolean verifyCurrentPassword(String email, String currentPassword) {
        LOGGER.info("Entering verifyCurrentPassword with email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow();
        String passwordHash = passwordEncoder.encode(currentPassword);
        user.setPassword(passwordHash);
        boolean matches = passwordEncoder.matches(currentPassword, user.getPassword());
        LOGGER.info("Password verification result: {}", matches);
        return matches;
    }
}