package com.example.quizlecikprojekt.wordset;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetWordSetsIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldReturnEmptyListWhenUserHasNoWordSets() throws Exception {
        String token = getJWTToken();

        MvcResult result = mockMvc.perform(get("/api/word-sets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        []
        """;

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldReturnSingleWordSetWithoutWords() throws Exception {
        String token = getJWTToken();

        createTestWordSet(token, "My First Set", "Basic vocabulary");

        MvcResult result = mockMvc.perform(get("/api/word-sets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        [
          {
            "id": 1,
            "title": "My First Set",
            "description": "Basic vocabulary",
            "language": "pl",
            "translationLanguage": "en",
            "words": [],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          }
        ]
        """;

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldReturnMultipleWordSetsOrderedByCreatedAtDesc() throws Exception {
        String token = getJWTToken();

        createTestWordSet(token, "First Set", "Created first");
        createTestWordSet(token, "Second Set", "Created second");
        createTestWordSet(token, "Third Set", "Created third");

        MvcResult result = mockMvc.perform(get("/api/word-sets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        [
          {
            "id": 3,
            "title": "Third Set",
            "description": "Created third",
            "language": "pl",
            "translationLanguage": "en",
            "words": [],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          },
          {
            "id": 2,
            "title": "Second Set",
            "description": "Created second",
            "language": "pl",
            "translationLanguage": "en",
            "words": [],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          },
          {
            "id": 1,
            "title": "First Set",
            "description": "Created first",
            "language": "pl",
            "translationLanguage": "en",
            "words": [],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          }
        ]
        """;

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldReturnWordSetWithWords() throws Exception {
        String token = getJWTToken();

        Long wordSetId = createTestWordSet(token, "English Basics", "Common words");

        addWordsToWordSet(token, wordSetId, "hello", "cześć");
        addWordsToWordSet(token, wordSetId, "goodbye", "do widzenia");

        MvcResult result = mockMvc.perform(get("/api/word-sets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        [
          {
            "id": %d,
            "title": "English Basics",
            "description": "Common words",
            "language": "pl",
            "translationLanguage": "en",
            "words": [
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
              }
            ],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          }
        ]
        """.formatted(wordSetId, wordSetId, wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectUnauthorizedRequest() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/word-sets"))
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

    @Test
    void shouldReturnWordSetsWithMixedContent() throws Exception {
        String token = getJWTToken();

        // Create word set with words
        Long wordSetId1 = createTestWordSet(token, "With Words", "Has vocabulary");
        addWordsToWordSet(token, wordSetId1, "cat", "kot");

        // Create empty word set
        createTestWordSet(token, "Empty Set", "No words yet");

        MvcResult result = mockMvc.perform(get("/api/word-sets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        [
          {
            "id": 2,
            "title": "Empty Set",
            "description": "No words yet",
            "language": "pl",
            "translationLanguage": "en",
            "words": [],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          },
          {
            "id": %d,
            "title": "With Words",
            "description": "Has vocabulary",
            "language": "pl",
            "translationLanguage": "en",
            "words": [
              {
                "id": 1,
                "word": "cat",
                "translation": "kot",
                "points": 0,
                "star": false,
                "lastPracticed": null,
                "wordSetId": %d
              }
            ],
            "createdAt": "2025-08-09T09:50:45",
            "updatedAt": "2025-08-09T09:50:45"
          }
        ]
        """.formatted(wordSetId1, wordSetId1);

        asserter.assertApiResponse(result, expectedJson);
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

    private void addWordsToWordSet(String token, Long wordSetId, String word, String translation) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        ObjectNode wordObj = objectMapper.createObjectNode();
        wordObj.put("word", word);
        wordObj.put("translation", translation);
        wordsArray.add(wordObj);

        req.set("words", wordsArray);

        // Debug: Print the request being sent
        String requestJson = objectMapper.writeValueAsString(req);
        System.out.println("Request JSON: " + requestJson);

        MvcResult result = mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8") // Add explicit encoding
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Debug: Print the response
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Add word response: " + responseJson);
        System.out.println("Add word response status: " + result.getResponse().getStatus());
    }
}