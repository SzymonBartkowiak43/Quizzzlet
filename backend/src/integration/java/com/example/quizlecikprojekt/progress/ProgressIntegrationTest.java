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
    void shouldRecordStudySessionSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("totalWordsStudied", 15);
        request.put("correctAnswers", 12);
        request.put("incorrectAnswers", 3);
        request.put("flashcardsCompleted", 15);
        request.put("quizzesCompleted", 1);
        request.put("studyTimeMinutes", 25);
        request.put("sessionType", "mixed");

        String expectedJson = """
        {
          "date": "2025-08-09",
          "wordsStudied": 15,
          "correctAnswers": 12,
          "incorrectAnswers": 3,
          "flashcardsCompleted": 15,
          "quizzesCompleted": 1,
          "studyTimeMinutes": 25,
          "accuracyPercentage": 80.0
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/record-session")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldAccumulateMultipleStudySessionsOnSameDay() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        // Record first session
        recordStudySession(token, wordSetId, 10, 8, 2, 10, 0, 15);

        // Record second session on same day
        ObjectNode secondRequest = objectMapper.createObjectNode();
        secondRequest.put("wordSetId", wordSetId);
        secondRequest.put("totalWordsStudied", 5);
        secondRequest.put("correctAnswers", 4);
        secondRequest.put("incorrectAnswers", 1);
        secondRequest.put("flashcardsCompleted", 0);
        secondRequest.put("quizzesCompleted", 1);
        secondRequest.put("studyTimeMinutes", 10);
        secondRequest.put("sessionType", "quiz");

        String expectedJson = """
        {
          "date": "2025-08-09",
          "wordsStudied": 15,
          "correctAnswers": 12,
          "incorrectAnswers": 3,
          "flashcardsCompleted": 10,
          "quizzesCompleted": 1,
          "studyTimeMinutes": 25,
          "accuracyPercentage": 80.0
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/record-session")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

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
    void shouldEvaluateWordSetSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("rating", 5);
        request.put("usefulnessRating", 4);
        request.put("difficultyLevel", "INTERMEDIATE");
        request.put("comment", "Excellent word set for learning!");
        request.put("wouldRecommend", true);
        request.put("completionTimeMinutes", 30);
        request.put("tags", "vocabulary,learning,useful");

        String expectedJson = """
        {
          "id": 1,
          "resourceType": "wordset",
          "resourceId": %d,
          "resourceTitle": "New Word Set",
          "rating": 5,
          "usefulnessRating": 4,
          "difficultyLevel": "INTERMEDIATE",
          "comment": "Excellent word set for learning!",
          "wouldRecommend": true,
          "completionTimeMinutes": 30,
          "tags": "vocabulary,learning,useful",
          "evaluatorName": "loginuser",
          "createdAt": "2025-08-09T14:05:52"
        }
        """.formatted(wordSetId);

        MvcResult result = mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldUpdateExistingEvaluationWhenEvaluatingSameResource() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        // First evaluation
        evaluateWordSet(token, wordSetId, 3, 3, "BEGINNER", "Initial review");

        // Update evaluation for same resource
        ObjectNode updateRequest = objectMapper.createObjectNode();
        updateRequest.put("wordSetId", wordSetId);
        updateRequest.put("rating", 5);
        updateRequest.put("usefulnessRating", 5);
        updateRequest.put("difficultyLevel", "INTERMEDIATE");
        updateRequest.put("comment", "Much better after more practice!");
        updateRequest.put("wouldRecommend", true);
        updateRequest.put("completionTimeMinutes", 45);

        MvcResult result = mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(5, response.get("rating").asInt());
        assertEquals(5, response.get("usefulnessRating").asInt());
        assertEquals("INTERMEDIATE", response.get("difficultyLevel").asText());
        assertEquals("Much better after more practice!", response.get("comment").asText());
        assertTrue(response.get("wouldRecommend").asBoolean());
    }

    @Test
    void shouldEvaluateVideoSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Learning Video", "https://youtube.com/watch?v=test");

        ObjectNode request = objectMapper.createObjectNode();
        request.put("videoId", videoId);
        request.put("rating", 4);
        request.put("usefulnessRating", 5);
        request.put("difficultyLevel", "ADVANCED");
        request.put("comment", "Great video content!");
        request.put("wouldRecommend", true);
        request.put("completionTimeMinutes", 60);
        request.put("tags", "video,tutorial,advanced");

        String expectedJson = """
        {
          "id": 1,
          "resourceType": "video",
          "resourceId": %d,
          "resourceTitle": "Learning Video",
          "rating": 4,
          "usefulnessRating": 5,
          "difficultyLevel": "ADVANCED",
          "comment": "Great video content!",
          "wouldRecommend": true,
          "completionTimeMinutes": 60,
          "tags": "video,tutorial,advanced",
          "evaluatorName": "loginuser",
          "createdAt": "2025-08-09T14:05:52"
        }
        """.formatted(videoId);

        MvcResult result = mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetEmptyEvaluationSummaryForUnevaluatedResource() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        MvcResult result = mockMvc.perform(get("/api/progress/evaluations/wordset/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
        {
          "resourceType": "wordset",
          "resourceId": %d,
          "resourceTitle": "New Word Set",
          "averageRating": 0.0,
          "averageUsefulnessRating": 0.0,
          "totalEvaluations": 0,
          "mostCommonDifficulty": null,
          "recommendationPercentage": 0.0,
          "recentEvaluations": []
        }
        """.formatted(wordSetId);

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetUserEvaluationsSuccessfully() throws Exception {
        String token = getJWTToken();
        Long wordSetId1 = createTestWordSet(token, "WordSet 1");
        Long wordSetId2 = createTestWordSet(token, "WordSet 2");
        Long videoId = createTestVideo(token, "Test Video", "https://youtube.com/watch?v=abc");

        evaluateWordSet(token, wordSetId1, 5, 4, "INTERMEDIATE", "Excellent!");
        evaluateWordSet(token, wordSetId2, 3, 3, "BEGINNER", "Okay for beginners");
        evaluateVideo(token, videoId, 4, 5, "ADVANCED", "Good video");

        MvcResult result = mockMvc.perform(get("/api/progress/evaluations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertTrue(response.isArray());
        assertEquals(3, response.size());

        // Check that we have both wordset and video evaluations
        boolean hasWordSetEval = false;
        boolean hasVideoEval = false;

        for (JsonNode eval : response) {
            assertEquals("loginuser", eval.get("evaluatorName").asText());
            String resourceType = eval.get("resourceType").asText();
            if ("wordset".equals(resourceType)) hasWordSetEval = true;
            if ("video".equals(resourceType)) hasVideoEval = true;
        }

        assertTrue(hasWordSetEval, "Should have wordset evaluations");
        assertTrue(hasVideoEval, "Should have video evaluations");
    }

    @Test
    void shouldGetMySpecificEvaluationForWordSet() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        evaluateWordSet(token, wordSetId, 5, 4, "INTERMEDIATE", "My evaluation");

        MvcResult result = mockMvc.perform(get("/api/progress/evaluations/my-evaluation/wordset/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals("wordset", response.get("resourceType").asText());
        assertEquals(wordSetId, response.get("resourceId").asLong());
        assertEquals(5, response.get("rating").asInt());
        assertEquals(4, response.get("usefulnessRating").asInt());
        assertEquals("INTERMEDIATE", response.get("difficultyLevel").asText());
        assertEquals("My evaluation", response.get("comment").asText());
        assertEquals("loginuser", response.get("evaluatorName").asText());
    }

    @Test
    void shouldReturn404ForNonExistentEvaluation() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        // Don't create any evaluation, just try to get it
        mockMvc.perform(get("/api/progress/evaluations/my-evaluation/wordset/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
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
    void shouldRejectEvaluationWithBothWordSetAndVideo() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);
        Long videoId = createTestVideo(token, "Test Video", "https://youtube.com/watch?v=test");

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("videoId", videoId); // Both provided - should be rejected
        request.put("rating", 5);
        request.put("usefulnessRating", 4);

        String expectedJson = """
        {
          "message": "Cannot evaluate both wordset and video in one request",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectEvaluationWithNeitherWordSetNorVideo() throws Exception {
        String token = getJWTToken();

        ObjectNode request = objectMapper.createObjectNode();
        // Neither wordSetId nor videoId provided
        request.put("rating", 5);
        request.put("usefulnessRating", 4);

        String expectedJson = """
        {
          "message": "Either wordSetId or videoId must be provided",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectEvaluationWithInvalidRatings() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("rating", 10); // Invalid: should be 1-5
        request.put("usefulnessRating", 0); // Invalid: should be 1-5

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
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

    @Test
    void shouldRequireAuthentication() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("totalWordsStudied", 5);
        request.put("correctAnswers", 4);
        request.put("incorrectAnswers", 1);

        String expectedJson = """
        {
          "message": "Unauthorized",
          "status": "UNAUTHORIZED"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/progress/record-session")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    // Helper methods
    private Long createTestWordSet(String token) throws Exception {
        return createTestWordSet(token, "Test WordSet");
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

    private Long createTestVideo(String token, String title, String url) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("title", title);
        request.put("url", url);

        MvcResult result = mockMvc.perform(post("/api/videos")
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

    private void evaluateWordSet(String token, Long wordSetId, int rating,
                                 int usefulness, String difficulty, String comment) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("rating", rating);
        request.put("usefulnessRating", usefulness);
        request.put("difficultyLevel", difficulty);
        request.put("comment", comment);
        request.put("wouldRecommend", rating >= 4);

        mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void evaluateVideo(String token, Long videoId, int rating,
                               int usefulness, String difficulty, String comment) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("videoId", videoId);
        request.put("rating", rating);
        request.put("usefulnessRating", usefulness);
        request.put("difficultyLevel", difficulty);
        request.put("comment", comment);
        request.put("wouldRecommend", rating >= 4);

        mockMvc.perform(post("/api/progress/evaluate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}