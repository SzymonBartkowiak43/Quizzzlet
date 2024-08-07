package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.Dto.UserDto;
import com.example.quizlecikprojekt.domain.user.Dto.UserRegistrationDto;
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

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserByid(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setUserName(userRegistrationDto.getUsername());
        String passwordHash = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(passwordHash);

        UserRole defaultRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
        user.getRoles().add(defaultRole);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }


    public boolean verifyCurrentPassword(String email, String currentPassword) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String passwordHash = passwordEncoder.encode(currentPassword);
        user.setPassword(passwordHash);
        return  passwordEncoder.matches(currentPassword, user.getPassword());
    }
}
