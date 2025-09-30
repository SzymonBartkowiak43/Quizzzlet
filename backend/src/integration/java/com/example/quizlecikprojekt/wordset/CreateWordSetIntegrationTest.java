package com.example.quizlecikprojekt.wordset;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateWordSetIntegrationTest extends BaseIntegrationTest {


    @Test
    void shouldCreateNewWordSetSuccessfully() throws Exception {
        String token = getJWTToken();

        ObjectNode req = objectMapper.createObjectNode();
        req.put("name", "My First Set");
        req.put("description", "Basics");

        MvcResult result = mockMvc.perform(post("/api/word-sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        String expectedJson = """
        {
          "id": 1,
          "title": "My First Set",
          "description": "Basics",
          "language": "pl",
          "translationLanguage": "en",
          "words": [],
          "createdAt": "2023-10-01T12:00:00",
          "updatedAt": "2023-10-01T12:00:00"
        }
        """;

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectTooLongName() throws Exception {
        String token = getJWTToken();

        String tooLong = "x".repeat(101);
        ObjectNode req = objectMapper.createObjectNode();
        req.put("name", tooLong);
        req.put("description", "desc");

        MvcResult result = mockMvc.perform(post("/api/word-sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;
        asserter.assertErrorResponse(result, expectedJson);
    }


}
