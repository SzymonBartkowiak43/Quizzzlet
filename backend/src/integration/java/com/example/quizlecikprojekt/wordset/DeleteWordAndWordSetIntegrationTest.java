package com.example.quizlecikprojekt.wordset;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteWordAndWordSetIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldDeleteWordSetSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Set to Delete", "This will be deleted");

        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "message": "Word set deleted successfully",
          "deletedWordSetId": %d
        }
        """.formatted(wordSetId);

        asserter.assertApiResponse(result, expectedJson);

        // Verify the word set is actually deleted
        mockMvc.perform(get("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteWordSetWithWordsSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Set with Words", "Has words to delete");

        // Add some words to the set
        addWordsToWordSet(token, wordSetId, List.of(
                new WordItem("hello", "cześć"),
                new WordItem("goodbye", "do widzenia")
        ));

        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "message": "Word set deleted successfully",
          "deletedWordSetId": %d
        }
        """.formatted(wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectDeleteOfNonexistentWordSet() throws Exception {
        String token = getJWTToken();

        MvcResult result = mockMvc.perform(delete("/api/word-sets/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        String expectedJson = """
        {
          "message": "Requested resource not found",
          "status": "NOT_FOUND"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectDeleteOfWordSetNotOwnedByUser() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();

        // User 1 creates a word set
        Long wordSetId = createTestWordSet(token1, "User 1 Set", "Description");

        // User 2 tries to delete it
        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isForbidden())
                .andReturn();

        String expectedJson = """
        {
          "message": "You don't have permission to delete this word set"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldDeleteSingleWordSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");
        Long wordId = addSingleWordToWordSet(token, wordSetId, "delete", "usuń");

        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId + "/words/" + wordId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "message": "Word deleted successfully",
          "deletedWordId": %d
        }
        """.formatted(wordId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldDeleteMultipleWordsSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");

        // Add multiple words
        List<Long> wordIds = addMultipleWordsToWordSet(token, wordSetId, List.of(
                new WordItem("word1", "słowo1"),
                new WordItem("word2", "słowo2"),
                new WordItem("word3", "słowo3")
        ));

        // Delete first two words
        List<Long> wordsToDelete = wordIds.subList(0, 2);

        ObjectNode deleteRequest = objectMapper.createObjectNode();
        ArrayNode wordIdsArray = objectMapper.createArrayNode();
        wordsToDelete.forEach(wordIdsArray::add);
        deleteRequest.set("wordIds", wordIdsArray);

        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "message": "2 words deleted successfully",
          "deletedCount": 2,
          "deletedWordIds": [%d, %d]
        }
        """.formatted(wordsToDelete.get(0), wordsToDelete.get(1));

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectDeleteWordOfNonexistentWord() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");

        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId + "/words/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        String expectedJson = """
        {
          "message": "Requested resource not found",
          "status": "NOT_FOUND"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectDeleteWordWhenUserDoesntOwnWordSet() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();

        // User 1 creates word set and adds word
        Long wordSetId = createTestWordSet(token1, "User 1 Set", "Description");
        Long wordId = addSingleWordToWordSet(token1, wordSetId, "word", "słowo");

        // User 2 tries to delete the word
        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId + "/words/" + wordId)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isForbidden())
                .andReturn();

        String expectedJson = """
        {
          "message": "You don't have permission to delete this word"
        }
        """;

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldHandlePartialDeleteOfMultipleWords() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Test Set", "Description");

        // Add one word
        Long validWordId = addSingleWordToWordSet(token, wordSetId, "valid", "ważny");
        Long invalidWordId = 999999L;

        // Try to delete both valid and invalid word IDs
        ObjectNode deleteRequest = objectMapper.createObjectNode();
        ArrayNode wordIdsArray = objectMapper.createArrayNode();
        wordIdsArray.add(validWordId);
        wordIdsArray.add(invalidWordId);
        deleteRequest.set("wordIds", wordIdsArray);

        MvcResult result = mockMvc.perform(delete("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Should delete only the valid word
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(1, response.get("deletedCount").asInt());
        assertTrue(response.get("message").asText().contains("1 words deleted successfully"));
    }

    @Test
    void shouldRejectDeleteWithoutAuthentication() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/word-sets/1"))
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
        List<Long> wordIds = addMultipleWordsToWordSet(token, wordSetId, List.of(new WordItem(word, translation)));
        return wordIds.get(0);
    }

    private List<Long> addMultipleWordsToWordSet(String token, Long wordSetId, List<WordItem> words) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        for (WordItem wordItem : words) {
            ObjectNode wordObj = objectMapper.createObjectNode();
            wordObj.put("word", wordItem.word);
            wordObj.put("translation", wordItem.translation);
            wordsArray.add(wordObj);
        }

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        return response.get("addedWords").findValues("id").stream()
                .map(JsonNode::asLong)
                .toList();
    }

    private void addWordsToWordSet(String token, Long wordSetId, List<WordItem> words) throws Exception {
        addMultipleWordsToWordSet(token, wordSetId, words);
    }

    private String getJWTTokenForAnotherUser() throws Exception {
        // Register another user
        ObjectNode registerReq = objectMapper.createObjectNode();
        registerReq.put("email", "deleteuser2@example.com");
        registerReq.put("password", "SecurePassword123!");
        registerReq.put("name", "Delete User Two");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        // Login as the second user
        ObjectNode loginReq = objectMapper.createObjectNode();
        loginReq.put("email", "deleteuser2@example.com");
        loginReq.put("password", "SecurePassword123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponseJson = loginResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(loginResponseJson).get("token").asText();
    }

    // Helper record for word data
    private record WordItem(String word, String translation) {}
}