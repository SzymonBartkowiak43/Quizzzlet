package com.example.quizlecikprojekt.login;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import com.example.quizlecikprojekt.login.models.UserJsonModel;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // given
        UserRegistrationDto registrationDto = createRegistrationDto(
                "testuser@example.com", "StrongPass123!", "testuser"
        );

        String expectedJson = UserJsonModel.maximal(
                1L, registrationDto.email(), registrationDto.username(), "USER"
        ).toString();

        // when
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        // given: register user first
        registerUser();

        // prepare login request
        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("username", "loginuser@example.com");
        loginRequest.put("password", "Password123!");

        String expectedJson = """
        {
          "success": true,
          "message": "Login successful",
          "data": {
            "username": "loginuser@example.com",
            "message": "Login successful"
          }
        }
        """;

        // when
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        asserter.assertApiResponse(loginResult, expectedJson);
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // given:
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("username", "loginuser@example.com");
        loginRequest.put("password", "WrongPassword!");

        String expectedJson = """
        {
            "success": false,
            "message": "Invalid credentials"
        }
        """;

        // when
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // then
        asserter.assertApiResponse(loginResult, expectedJson);
    }


    private UserRegistrationDto createRegistrationDto(String email, String password, String username) {
        return new UserRegistrationDto(email, password, username);
    }

    private void registerUser() throws Exception {
        UserRegistrationDto registrationDto = createRegistrationDto(
                "loginuser@example.com", "Password123!", "loginuser"
        );

         mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn();
    }

}