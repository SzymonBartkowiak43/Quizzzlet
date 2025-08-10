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

public class EvaluationIntegrationTest extends BaseIntegrationTest {

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
          "createdAt": "2025-08-10T13:53:17"
        }
        """.formatted(wordSetId);

        MvcResult result = mockMvc.perform(post("/api/evaluations")
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

        MvcResult result = mockMvc.perform(post("/api/evaluations")
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
          "createdAt": "2025-08-10T13:53:17"
        }
        """.formatted(videoId);

        MvcResult result = mockMvc.perform(post("/api/evaluations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetWordSetEvaluationSummaryWithMultipleEvaluations() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();
        Long wordSetId = createTestWordSet(token1);

        // Add evaluations from both users
        evaluateWordSet(token1, wordSetId, 5, 4, "INTERMEDIATE", "Great wordset!");
        evaluateWordSet(token2, wordSetId, 4, 5, "BEGINNER", "Very helpful for beginners");

        MvcResult result = mockMvc.perform(get("/api/evaluations/wordset/" + wordSetId)
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals("wordset", response.get("resourceType").asText());
        assertEquals(wordSetId, response.get("resourceId").asLong());
        assertEquals("New Word Set", response.get("resourceTitle").asText());
        assertEquals(2, response.get("totalEvaluations").asInt());
        assertEquals(4.5, response.get("averageRating").asDouble(), 0.1);
        assertEquals(4.5, response.get("averageUsefulnessRating").asDouble(), 0.1);
        assertEquals(100.0, response.get("recommendationPercentage").asDouble());
        assertTrue(response.get("recentEvaluations").isArray());
        assertEquals(2, response.get("recentEvaluations").size());
    }

    @Test
    void shouldGetEmptyEvaluationSummaryForUnevaluatedResource() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);

        MvcResult result = mockMvc.perform(get("/api/evaluations/wordset/" + wordSetId)
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
        evaluateVideo(token, videoId, 4, "Good video");

        MvcResult result = mockMvc.perform(get("/api/evaluations/my-evaluations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertTrue(response.isArray());
        assertEquals(3, response.size());

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

        MvcResult result = mockMvc.perform(get("/api/evaluations/my-evaluation/wordset/" + wordSetId)
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
        mockMvc.perform(get("/api/evaluations/my-evaluation/wordset/" + wordSetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHighlyRatedResourcesSuccessfully() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();

        Long wordSetId = createTestWordSet(token1);
        Long videoId = createTestVideo(token1, "Great Video", "https://youtube.com/watch?v=great");

        // Add high ratings
        evaluateWordSet(token1, wordSetId, 5, 5, "INTERMEDIATE", "Amazing!");
        evaluateWordSet(token2, wordSetId, 4, 4, "INTERMEDIATE", "Very good");
        evaluateVideo(token1, videoId, 5, "Excellent video");

        MvcResult result = mockMvc.perform(get("/api/evaluations/highly-rated")
                .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertTrue(response.isArray());

        if (!response.isEmpty()) {
            JsonNode firstEval = response.get(0);
            assertTrue(firstEval.get("rating").asInt() >= 4);
            assertNotNull(firstEval.get("resourceType"));
            assertNotNull(firstEval.get("evaluatorName"));
        }
    }

    @Test
    void shouldRejectEvaluationWithBothWordSetAndVideo() throws Exception {
        String token = getJWTToken();
        Long wordSetId = createTestWordSet(token);
        Long videoId = createTestVideo(token, "Test Video", "https://youtube.com/watch?v=test");

        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("videoId", videoId);
        request.put("rating", 5);
        request.put("usefulnessRating", 4);

        String expectedJson = """
        {
          "message": "Cannot evaluate both wordset and video in one request",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/evaluations")
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

        MvcResult result = mockMvc.perform(post("/api/evaluations")
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
        request.put("rating", 10);
        request.put("usefulnessRating", 0);

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/evaluations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

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

    private void evaluateWordSet(String token, Long wordSetId, int rating,
                                 int usefulness, String difficulty, String comment) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("wordSetId", wordSetId);
        request.put("rating", rating);
        request.put("usefulnessRating", usefulness);
        request.put("difficultyLevel", difficulty);
        request.put("comment", comment);
        request.put("wouldRecommend", rating >= 4);

        mockMvc.perform(post("/api/evaluations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void evaluateVideo(String token, Long videoId, int rating,
                               String comment) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("videoId", videoId);
        request.put("rating", rating);
        request.put("usefulnessRating", 5);
        request.put("difficultyLevel", "ADVANCED");
        request.put("comment", comment);
        request.put("wouldRecommend", rating >= 4);

        mockMvc.perform(post("/api/evaluations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String getJWTTokenForAnotherUser() throws Exception {
        ObjectNode registerReq = objectMapper.createObjectNode();
        registerReq.put("email", "progressuser2@example.com");
        registerReq.put("password", "SecurePassword123!");
        registerReq.put("name", "Progress User Two");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        ObjectNode loginReq = objectMapper.createObjectNode();
        loginReq.put("email", "progressuser2@example.com");
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