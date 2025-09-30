package com.example.quizlecikprojekt.progress;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProgressIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldGetProgressSummarySuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        // Record a few study sessions
        recordStudySession(token, wordSetId, 10, 8, 2, 10, 0, 15);
        recordStudySession(token, wordSetId, 5, 4, 1, 5, 1, 10);

        MvcResult result = mockMvc.perform(get("/api/progress/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals("loginuser", response.get("userName").asText());
        assertEquals(15, response.get("totalWordsStudied").asInt());
        assertEquals(15, response.get("totalFlashcardsCompleted").asInt());
        assertEquals(1, response.get("totalQuizzesCompleted").asInt());
        assertEquals(25, response.get("totalStudyTimeMinutes").asInt());
        assertTrue(response.get("overallAccuracy").asDouble() > 75.0);
        assertTrue(response.get("currentStreak").asInt() >= 1);
        assertNotNull(response.get("recentProgress"));
        assertTrue(response.get("recentProgress").isArray());
    }

    @Test
    void shouldGetWeeklyProgressSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        recordStudySession(token, wordSetId, 10, 8, 2, 10, 0, 15);

        MvcResult result = mockMvc.perform(get("/api/progress/weekly")
                        .param("weeks", "4")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertTrue(response.isArray());
    }

    @Test
    void shouldGetProgressStatsSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        recordStudySession(token, wordSetId, 10, 8, 2, 10, 0, 15);

        MvcResult result = mockMvc.perform(get("/api/progress/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertTrue(response.get("totalDaysStudied").asInt() >= 0);
        assertTrue(response.get("currentStreak").asInt() >= 0);
        assertTrue(response.get("longestStreak").asInt() >= 0);
        assertNotNull(response.get("mostActiveDay"));
        assertNotNull(response.get("preferredStudyTime"));
        assertNotNull(response.get("weeklyProgress"));
        assertNotNull(response.get("monthlyProgress"));
        assertTrue(response.get("weeklyProgress").isArray());
        assertTrue(response.get("monthlyProgress").isArray());
    }

    @Test
    void shouldRejectStudySessionWithInvalidData() throws Exception {
        String token = getJWTToken();

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", 999999L); // Non-existent word set
        request.put("totalWordsStudied", 0); // Invalid: must be at least 1
        request.put("correctAnswers", -1); // Invalid: cannot be negative
        request.put("incorrectAnswers", 5);

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/record-session")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    // Helper methods
    private Long createTestWordSet(String token) throws Exception {
        return createTestWordSet(token, "New Word Set");
    }

    private Long createTestWordSet(String token, String title) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("title", title);
        request.put("description", "Test description");

        MvcResult result = mockMvc.perform(post("/api/word-sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).get("id").asLong();
    }

    private void recordStudySession(String token, Long wordSetId, int totalWords,
                                    int correct, int incorrect, int flashcards,
                                    int quizzes, int studyTime) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("totalWordsStudied", totalWords);
        request.put("correctAnswers", correct);
        request.put("incorrectAnswers", incorrect);
        request.put("flashcardsCompleted", flashcards);
        request.put("quizzesCompleted", quizzes);
        request.put("studyTimeMinutes", studyTime);
        request.put("sessionType", "mixed");

        mockMvc.perform(post("/api/progress/record-session")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}