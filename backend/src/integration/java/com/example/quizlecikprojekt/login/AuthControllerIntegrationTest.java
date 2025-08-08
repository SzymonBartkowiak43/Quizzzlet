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
                1L, "testuser@example.com", "testuser@example.com", new String[]{}
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
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("email", "loginuser@example.com");
        loginRequest.put("password", "Password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        asserter.assertAuthResponse(loginResult, "loginuser@example.com");
    }


    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // given:
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("email", "loginuser@example.com");
        loginRequest.put("password", "WrongPassword!");

        String expectedJson = """
        {
            "message": "Bad Credentials",
            "status": "UNAUTHORIZED"
        }
        """;

        // when
        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // then
        asserter.assertErrorResponse(loginResult, expectedJson);
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