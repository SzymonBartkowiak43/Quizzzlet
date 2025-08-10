package com.example.quizlecikprojekt.learn;

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

public class LearnIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldStartFlashcardSessionSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSetWithWords(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);


        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Use asserter but check specific fields manually since sessionId is random
        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertNotNull(response.get("sessionId"));
        assertEquals(wordSetId, response.get("wordSetId").asLong());
        assertEquals("Learn Test Set", response.get("wordSetTitle").asText());
        assertEquals(3, response.get("totalWords").asInt());
        assertEquals(0, response.get("currentIndex").asInt());
        assertEquals(0, response.get("score").asInt());
        assertFalse(response.get("isCompleted").asBoolean());
        assertNotNull(response.get("currentCard"));
    }

    @Test
    void shouldAnswerFlashcardCorrectly() throws Exception {
        String token = getJWTToken();
        String sessionId = startFlashcardSession(token);

        ObjectNode answerRequest = objectMapper.createObjectNode();
        answerRequest.put("sessionId", sessionId);
        answerRequest.put("isCorrect", true);

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(sessionId, response.get("sessionId").asText());
        assertEquals(1, response.get("currentIndex").asInt());
        assertEquals(1, response.get("score").asInt());
        assertFalse(response.get("isCompleted").asBoolean());
    }

    @Test
    void shouldAnswerFlashcardIncorrectly() throws Exception {
        String token = getJWTToken();
        String sessionId = startFlashcardSession(token);

        ObjectNode answerRequest = objectMapper.createObjectNode();
        answerRequest.put("sessionId", sessionId);
        answerRequest.put("isCorrect", false);

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(sessionId, response.get("sessionId").asText());
        assertEquals(1, response.get("currentIndex").asInt());
        assertEquals(0, response.get("score").asInt()); // Score should remain 0
        assertFalse(response.get("isCompleted").asBoolean());
    }

    @Test
    void shouldCompleteFlashcardSession() throws Exception {
        String token = getJWTToken();
        String sessionId = startFlashcardSession(token);

        // Answer all 3 cards correctly
        for (int i = 0; i < 3; i++) {
            ObjectNode answerRequest = objectMapper.createObjectNode();
            answerRequest.put("sessionId", sessionId);
            answerRequest.put("isCorrect", true);

            MvcResult result = mockMvc.perform(post("/api/learn/flashcards/answer")
                            .header("Authorization", "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(answerRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JsonNode response = objectMapper.readTree(responseJson);

            if (i == 2) { // Last card
                assertTrue(response.get("isCompleted").asBoolean());
                assertEquals(3, response.get("score").asInt());;
            }
        }
    }

    @Test
    void shouldGetFlashcardSessionStatus() throws Exception {
        String token = getJWTToken();
        String sessionId = startFlashcardSession(token);

        MvcResult result = mockMvc.perform(get("/api/learn/flashcards/" + sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(sessionId, response.get("sessionId").asText());
        assertEquals(0, response.get("currentIndex").asInt());
        assertEquals(0, response.get("score").asInt());
        assertFalse(response.get("isCompleted").asBoolean());
        assertNotNull(response.get("currentCard"));
    }


    @Test
    void shouldStartQuizSessionSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSetWithWords(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("numberOfQuestions", 2);

        MvcResult result = mockMvc.perform(post("/api/learn/quiz/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertNotNull(response.get("sessionId"));
        assertEquals(wordSetId, response.get("wordSetId").asLong());
        assertEquals("Learn Test Set", response.get("wordSetTitle").asText());
        assertEquals(2, response.get("totalQuestions").asInt());
        assertEquals(0, response.get("currentQuestion").asInt());
        assertEquals(0, response.get("score").asInt());
        assertFalse(response.get("isCompleted").asBoolean());

        JsonNode currentQuestions = response.get("currentQuestions");
        assertNotNull(currentQuestions.get("question"));
        assertTrue(currentQuestions.get("options").isArray());
    }

    @Test
    void shouldAnswerQuizQuestion() throws Exception {
        String token = getJWTToken();
        String sessionId = startQuizSession(token);

        // Get the current question to find an option
        MvcResult getResult = mockMvc.perform(get("/api/learn/quiz/" + sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String getResponseJson = getResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode getResponse = objectMapper.readTree(getResponseJson);

        JsonNode currentQuestions = getResponse.get("currentQuestions");
        String firstOption = currentQuestions.get("options").get(0).asText();

        ObjectNode answerRequest = objectMapper.createObjectNode();
        answerRequest.put("sessionId", sessionId);
        answerRequest.put("answer", firstOption);

        MvcResult result = mockMvc.perform(post("/api/learn/quiz/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(sessionId, response.get("sessionId").asText());
        assertEquals(1, response.get("currentQuestion").asInt());
    }

    @Test
    void shouldRejectFlashcardForNonexistentWordSet() throws Exception {
        String token = getJWTToken();

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", 999999L);

        String expectedJson = """
        {
          "message": "Requested resource not found",
          "status": "NOT_FOUND"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectFlashcardForEmptyWordSet() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token, "Empty Set", "No words");

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);

        String expectedJson = """
        {
          "message": "Word set has no words to learn",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectAnswerForInvalidSession() throws Exception {
        String token = getJWTToken();

        ObjectNode answerRequest = objectMapper.createObjectNode();
        answerRequest.put("sessionId", "invalid-session-id");
        answerRequest.put("isCorrect", true);

        mockMvc.perform(post("/api/learn/flashcards/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidFlashcardRequest() throws Exception {
        String token = getJWTToken();

        ObjectNode request = objectMapper.createObjectNode();
        // Missing wordSetId

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRequireAuthentication() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", 1L);

        String expectedJson = """
        {
          "message": "Unauthorized",
          "status": "UNAUTHORIZED"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/start")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldValidateQuizAnswerRequest() throws Exception {
        String token = getJWTToken();

        ObjectNode answerRequest = objectMapper.createObjectNode();
        answerRequest.put("sessionId", ""); // Blank session ID
        answerRequest.put("answer", "test");

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/learn/quiz/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldEndQuizSessionAndGetSummary() throws Exception {
        String token = getJWTToken();
        String sessionId = startQuizSession(token);

        // Answer one question (doesn't matter if correct or not for summary test)
        answerQuizQuestion(token, sessionId, "test answer");

        MvcResult result = mockMvc.perform(delete("/api/learn/quiz/" + sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(sessionId, response.get("sessionId").asText());
        assertEquals("quiz", response.get("sessionType").asText());
        assertEquals("Learn Test Set", response.get("wordSetTitle").asText());
        assertEquals(3, response.get("totalItems").asInt());
        assertNotNull(response.get("completedAt"));
        assertTrue(response.has("accuracy"));
    }

    // Helper methods
    private Long createTestWordSetWithWords(String token) throws Exception {
        Long wordSetId = createTestWordSet(token, "Learn Test Set", "For learning tests");

        List<WordItem> words = List.of(
                new WordItem("hello", "cześć"),
                new WordItem("goodbye", "do widzenia"),
                new WordItem("thank you", "dziękuję")
        );

        addWordsToWordSet(token, wordSetId, words);
        return wordSetId;
    }

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

    private void addWordsToWordSet(String token, Long wordSetId, List<WordItem> words) throws Exception {
        ObjectNode req = objectMapper.createObjectNode();
        ArrayNode wordsArray = objectMapper.createArrayNode();

        for (WordItem wordItem : words) {
            ObjectNode wordObj = objectMapper.createObjectNode();
            wordObj.put("word", wordItem.word);
            wordObj.put("translation", wordItem.translation);
            wordsArray.add(wordObj);
        }

        req.set("words", wordsArray);

        mockMvc.perform(post("/api/word-sets/" + wordSetId + "/words")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    private String startFlashcardSession(String token) throws Exception {
        Long wordSetId = createTestWordSetWithWords(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);

        MvcResult result = mockMvc.perform(post("/api/learn/flashcards/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(responseJson).get("sessionId").asText();
    }

    private String startQuizSession(String token) throws Exception {
        Long wordSetId = createTestWordSetWithWords(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("numberOfQuestions", 3);

        MvcResult result = mockMvc.perform(post("/api/learn/quiz/start")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(responseJson).get("sessionId").asText();
    }

    private void answerQuizQuestion(String token, String sessionId, String answer) throws Exception {
        ObjectNode answerRequest = objectMapper.createObjectNode();
        answerRequest.put("sessionId", sessionId);
        answerRequest.put("answer", answer);

        mockMvc.perform(post("/api/learn/quiz/answer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk());
    }
    private record WordItem(String word, String translation) {}
}