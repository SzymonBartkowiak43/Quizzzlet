package com.example.quizlecikprojekt.domain.user;

import com.example.quizlecikprojekt.domain.user.dto.UserDto;
import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.example.quizlecikprojekt.domain.user.dto.UserRoleDto;
import com.example.quizlecikprojekt.domain.user.exception.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

  private static final String DEFAULT_USER_ROLE = "USER";
  private static final String ADMIN_ROLE = "ADMIN";

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final MaperUserToUserRegisterDto mapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, MaperUserToUserRegisterDto mapper, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  public User createNewUser(UserRegisterDto userDto) {
    User user = createUser(userDto);
    return user;
  }

  public Optional<UserRegisterDto> getUser(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);

    return optionalUser.map(mapper::map);
  }

  public User getUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
  }


  public Optional<User> getUserWithId(Long id) {
    return userRepository.findById(id);
  }
  public String getNameById(Long id) {
    User userById = userRepository.getUserById(id);
    return userById.getName();
  }

  public Long getUserIdByUsername(String username) {
    return userRepository.getUserByName(username)
            .map(User::getId)
            .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
  }

  public UserDto findByEmail(String email) {
    User user = userRepository.getUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Not found"));


    Set<UserRoleDto> userRoleDtos = user.getRoles().stream()
            .map(role -> new UserRoleDto(role.getName()))
            .collect(Collectors.toSet());


    return UserDto.builder()
            .userId(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .password(user.getPassword())
            .roles(userRoleDtos)
            .build();
  }


  private User createUser(UserRegisterDto userDto) {
    User user = new User.UserBuilder()
            .name(userDto.name())
            .email(userDto.email())
            .password(userDto.password())
                    .build();

    user.addUserRole(getDefaultRole());

    userRepository.save(user);
    return user;
  }

  private UserRole getDefaultRole() {
    return userRoleRepository
            .findByName(DEFAULT_USER_ROLE)
            .orElseThrow(() -> new BadCredentialsException("User Role not Exists!"));
  }

  public User updateUser(UserRegisterDto userDto) {
    User user = userRepository.getUserByEmail(userDto.email())
            .orElseThrow(() -> new RuntimeException("user not found!!"));

    user.setName(userDto.name());
    user.setEmail(userDto.email());
    user.setPassword(userDto.password());

    userRepository.save(user);
    return user;
  }


  public boolean verifyCurrentPassword(String email, String currentPassword) {
    User user = userRepository. getUserByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

    boolean isValid = passwordEncoder.matches(currentPassword, user.getPassword());

    return isValid;
  }

  public boolean usernameExists(String username) {
    return userRepository.getUserByName(username).isPresent();
  }

}
