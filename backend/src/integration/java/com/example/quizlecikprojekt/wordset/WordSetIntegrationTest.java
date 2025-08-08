package com.example.quizlecikprojekt.wordset;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WordSetIntegrationTest extends BaseIntegrationTest {





    private String getJWTToken() throws Exception {
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("email", "loginuser@example.com");
        loginRequest.put("password", "Password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return loginRequest.asText();
    }
}
