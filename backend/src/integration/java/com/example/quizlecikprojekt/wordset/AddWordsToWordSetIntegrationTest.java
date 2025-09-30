package com.example.quizlecikprojekt.wordset;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AddWordsToWordSetIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldAddSingleWordToWordSetSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode word1 = objectMapper.createObjectNode();
        word1.put("word", "hello");
        word1.put("translation", "cześć");
        wordsArray.add(word1);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String expectedJson = """
        {
          "addedWords": [
            {
              "id": 1,
              "word": "hello",
              "translation": "cześć",
              "points": 0,
              "star": false,
              "lastPracticed": null,
              "wordSetId": %d
            }
          ],
          "totalAdded": 1,
          "message": "Word added successfully"
        }
        """.formatted(wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldAddMultipleWordsToWordSetSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode word1 = objectMapper.createObjectNode();
        word1.put("word", "hello");
        word1.put("translation", "cześć");
        wordsArray.add(word1);

        ObjectNode word2 = objectMapper.createObjectNode();
        word2.put("word", "goodbye");
        word2.put("translation", "do widzenia");
        wordsArray.add(word2);

        ObjectNode word3 = objectMapper.createObjectNode();
        word3.put("word", "thank you");
        word3.put("translation", "dziękuję");
        wordsArray.add(word3);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String expectedJson = """
        {
          "addedWords": [
            {
              "id": 1,
              "word": "hello",
              "translation": "cześć",
              "points": 0,
              "star": false,
              "lastPracticed": null,
              "wordSetId": %d
            },
            {
              "id": 2,
              "word": "goodbye",
              "translation": "do widzenia",
              "points": 0,
              "star": false,
              "lastPracticed": null,
              "wordSetId": %d
            },
            {
              "id": 3,
              "word": "thank you",
              "translation": "dziękuję",
              "points": 0,
              "star": false,
              "lastPracticed": null,
              "wordSetId": %d
            }
          ],
          "totalAdded": 3,
          "message": "3 words added successfully"
        }
        """.formatted(wordSetId, wordSetId, wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectAddWordsToNonExistentWordSet() throws Exception {
        String token = getJWTToken();

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode word1 = objectMapper.createObjectNode();
        word1.put("word", "hello");
        word1.put("translation", "cześć");
        wordsArray.add(word1);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/999/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
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
    void shouldRejectEmptyWordsList() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();
        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
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

    @Test
    void shouldRejectNullWordsList() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        req.putNull("words");

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
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

    @Test
    void shouldRejectBlankWordInList() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode word1 = objectMapper.createObjectNode();
        word1.put("word", "");
        word1.put("translation", "cześć");
        wordsArray.add(word1);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
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

    @Test
    void shouldRejectBlankTranslationInList() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode word1 = objectMapper.createObjectNode();
        word1.put("word", "hello");
        word1.put("translation", "");
        wordsArray.add(word1);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
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

    @Test
    void shouldRejectMixedValidAndInvalidWordsInList() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode word1 = objectMapper.createObjectNode();
        word1.put("word", "hello");
        word1.put("translation", "cześć");
        wordsArray.add(word1);

        ObjectNode word2 = objectMapper.createObjectNode();
        word2.put("word", "");  // Invalid blank word
        word2.put("translation", "do widzenia");
        wordsArray.add(word2);

        req.set("words", wordsArray);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
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

    private Long createTestWordSet(String token) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        req.put("name", "Test Word Set");
        req.put("description", "Test Description");

        MvcResult result = mockMvc.perform(post("/api/word-sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).get("id").asLong();
    }
}