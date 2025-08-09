package com.example.quizlecikprojekt.wordset;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateWordAndWordSetIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldUpdateWordSetSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Original Title", "Original Description");

        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("title", "Updated Title");
        updateRequest.put("description", "Updated Description");
        updateRequest.put("language", "en");
        updateRequest.put("translationLanguage", "pl");

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "id": %d,
          "title": "Updated Title",
          "description": "Updated Description",
          "language": "en",
          "translationLanguage": "pl",
          "words": [],
          "createdAt": "2025-08-09T11:51:18",
          "updatedAt": "2025-08-09T11:51:18"
        }
        """.formatted(wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Original Title", "Original Description");

        // Update only title
        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("title", "New Title Only");

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals("New Title Only", response.get("title").asText());
        assertEquals("Original Description", response.get("description").asText());
        assertEquals("pl", response.get("language").asText());
        assertEquals("en", response.get("translationLanguage").asText());
    }

    @Test
    void shouldRejectUpdateWithInvalidData() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Original Title", "Original Description");

        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("title", ""); // Empty title should be invalid
        updateRequest.put("description", "A".repeat(501)); // Too long description

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
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

    @Test
    void shouldRejectUpdateOfNonexistentWordSet() throws Exception {
        String token = getJWTToken();

        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("title", "Updated Title");

        mockMvc.perform(put("/api/word-sets/999999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUpdateOfWordSetNotOwnedByUser() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();

        // User 1 creates a word set
        Long wordSetId = createTestWordSet(token1, "User 1 Set", "Description");

        // User 2 tries to update it
        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("title", "Hacked Title");

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token2)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andReturn();

        String expectedJson = """
        {
          "message": "You don't have permission to update this word set"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldUpdateWordSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");
        Long wordId = addSingleWordToWordSet(token, wordSetId, "original", "pierwotny");

        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("word", "updated");
        updateRequest.put("translation", "zaktualizowany");

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId + "/words/" + wordId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "id": %d,
          "word": "updated",
          "translation": "zaktualizowany",
          "points": 0,
          "star": false,
          "lastPracticed": null,
          "wordSetId": %d
        }
        """.formatted(wordId, wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectWordUpdateWithInvalidData() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");
        Long wordId = addSingleWordToWordSet(token, wordSetId, "original", "pierwotny");

        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("word", ""); // Empty word
        updateRequest.put("translation", "A".repeat(101)); // Too long translation

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId + "/words/" + wordId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
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

    @Test
    void shouldRejectWordUpdateOfNonexistentWord() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");

        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("word", "updated");
        updateRequest.put("translation", "zaktualizowany");

        mockMvc.perform(put("/api/word-sets/" + wordSetId + "/words/999999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectWordUpdateWhenUserDoesntOwnWordSet() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();

        // User 1 creates word set and adds word
        Long wordSetId = createTestWordSet(token1, "User 1 Set", "Description");
        Long wordId = addSingleWordToWordSet(token1, wordSetId, "original", "pierwotny");

        // User 2 tries to update the word
        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("word", "hacked");
        updateRequest.put("translation", "zhakowany");

        MvcResult result = mockMvc.perform(put("/api/word-sets/" + wordSetId + "/words/" + wordId)
                        .header("Authorization", "Bearer " + token2)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andReturn();

        String expectedJson = """
        {
          "message": "You don't have permission to update this word"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectUpdateWithoutAuthentication() throws Exception {
        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("title", "Updated Title");

        MvcResult result = mockMvc.perform(put("/api/word-sets/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String expectedJson = """
        {
          "message": "Unauthorized",
          "status": "UNAUTHORIZED"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    // Helper methods
    private Long createTestWordSet(String token, String title, String description) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("name", title);
        req.put("description", description);

        MvcResult result = mockMvc.perform(post("/api/word-sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).get("id").asLong();
    }

    private Long addSingleWordToWordSet(String token, Long wordSetId, String word, String translation) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode wordObj = objectMapper.createObjectNode();
        wordObj.put("word", word);
        wordObj.put("translation", translation);
        wordsArray.add(wordObj);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);
        return response.get("addedWords").get(0).get("id").asLong();
    }

    private String getJWTTokenForAnotherUser() throws Exception {
        // Register another user
        ObjectNode registerReq = objectMapper.createObjectNode();
        registerReq.put("email", "user2@example.com");
        registerReq.put("password", "SecurePassword123!");
        registerReq.put("name", "User Two");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // Login as the second user
        ObjectNode loginReq = objectMapper.createObjectNode();
        loginReq.put("email", "user2@example.com");
        loginReq.put("password", "SecurePassword123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponseJson = loginResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(loginResponseJson).get("token").asText();
    }
}