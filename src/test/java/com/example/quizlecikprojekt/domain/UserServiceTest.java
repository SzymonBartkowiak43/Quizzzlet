package com.example.quizlecikprojekt.domain;

import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.UserRepository;
import com.example.quizlecikprojekt.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    List<User> users = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        User user1 = new User();
        user1.setId(1L);
        user1.setUserName("user1");
        user1.setEmail("user1@test.pl");
        user1.setPassword("{noop}password1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("user2");
        user2.setEmail("user2@test.pl");
        user2.setPassword("password2");

        User user3 = new User();
        user3.setId(3L);
        user3.setUserName("user3");
        user3.setEmail("user3@test.pl");
        user3.setPassword("password3");

        users = List.of(user1, user2, user3);
    }

    @Test
    public void GetUserByEmailTest() {
        //Given
        String email = "user1@test.pl";
        User user = users.get(0);

        //When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        User result = userService.getUserByEmail(email);

        //Then
        assertEquals(result.getEmail(), user.getEmail());
        assertEquals(result.getUserName(), user.getUserName());
        assertEquals(result.getPassword(), user.getPassword());
    }

    @Test
    public void GetUserByEmailTest2() {
        //Given
        String email = "notfound@test.pl";

        //When
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //Then
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    public void GetUserByIdTest() {
        //Given
        Long id = 1L;
        User user = users.get(0);

        //When
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //Then
        assertEquals(user.getId(), id);
    }

//    verifyCurrentPasswordTest
    @Test
    public void verifyCurrentPasswordWithCorrectPasswordTest() {
        //Given
        String email = "user1@test.pl";
        String password = "{noop}password1";
        User user = users.get(0);

        //When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        boolean result = userService.verifyCurrentPassword(email, password);

        //Then
        assertTrue(result);
    }

    @Test
    public void verifyCurrentPasswordWithIncorrectPasswordTest() {
        //Given
        String email = "user1@test.pl";
        String password = "{noop}wrong";
        User user = users.get(0);

        //When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        boolean result = userService.verifyCurrentPassword(email, password);

        //Then
        assertFalse(result);
    }

    @Test
    public void verifyCurrentPasswordUserNotFoundTest() {
        //Given
        String email = "notFound@test.pl";
        String password = "{noop}password";

        //When
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //Then
        assertThrows(NoSuchElementException.class, () -> userService.verifyCurrentPassword(email, password));
    }


}