package com.example.quizlecikprojekt.video;

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

public class VideoIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldAddVideoSuccessfully() throws Exception {
        String token = getJWTToken();

        ObjectNode request = objectMapper.createObjectNode();
        request.put("url", "https://www.youtube.com/watch?v=test123");
        request.put("title", "Test Video Title");

        String expectedJson = """
        {
          "id": 1,
          "title": "Test Video Title",
          "url": "https://www.youtube.com/watch?v=test123",
          "ownerName": "loginuser",
          "ownerId": 1,
          "averageRating": 0.0
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/videos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetVideoDetailsSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");

        String expectedJson = """
        {
          "id": %d,
          "title": "Test Video",
          "url": "https://example.com/video",
          "ownerName": "loginuser",
          "ownerId": 1,
          "comments": [],
          "userRating": 0,
          "averageRating": 0.0,
          "commentsCount": 0
        }
        """.formatted(videoId);

        MvcResult result = mockMvc.perform(get("/api/videos/" + videoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetAllVideosSuccessfully() throws Exception {
        String token = getJWTToken();
        createTestVideo(token, "Video 1", "https://example.com/video1");
        createTestVideo(token, "Video 2", "https://example.com/video2");

        MvcResult result = mockMvc.perform(get("/api/videos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(2, response.get("totalVideos").asInt());
        assertTrue(response.get("videos").isArray());
        assertEquals(2, response.get("videos").size());
        assertTrue(response.get("topRatedVideos").isArray());
    }

    @Test
    void shouldSearchVideosSuccessfully() throws Exception {
        String token = getJWTToken();
        createTestVideo(token, "Java Tutorial", "https://example.com/java");
        createTestVideo(token, "Python Guide", "https://example.com/python");
        createTestVideo(token, "JavaScript Basics", "https://example.com/js");

        MvcResult result = mockMvc.perform(get("/api/videos/search")
                        .param("query", "Java")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertEquals(2, response.get("totalVideos").asInt()); // Java Tutorial and JavaScript Basics
        JsonNode videos = response.get("videos");
        assertEquals(2, videos.size());

        // Check that search results contain Java-related videos
        boolean foundJavaTutorial = false;
        boolean foundJavaScript = false;
        for (JsonNode video : videos) {
            String title = video.get("title").asText();
            if ("Java Tutorial".equals(title)) foundJavaTutorial = true;
            if ("JavaScript Basics".equals(title)) foundJavaScript = true;
        }
        assertTrue(foundJavaTutorial && foundJavaScript);
    }

    @Test
    void shouldDeleteVideoSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Video to Delete", "https://example.com/delete");

        mockMvc.perform(delete("/api/videos/" + videoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify video is deleted
        mockMvc.perform(get("/api/videos/" + videoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddCommentToVideoSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");

        ObjectNode commentRequest = objectMapper.createObjectNode();
        commentRequest.put("content", "Great video!");

        String expectedJson = """
        {
          "id": 1,
          "content": "Great video!",
          "authorName": "loginuser",
          "createdAt": "2025-08-09T13:42:37"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/videos/" + videoId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetVideoCommentsSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");
        addCommentToVideo(token, videoId, "First comment");
        addCommentToVideo(token, videoId, "Second comment");

        MvcResult result = mockMvc.perform(get("/api/videos/" + videoId + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);

        assertTrue(response.isArray());
        assertEquals(2, response.size());
        assertEquals("First comment", response.get(0).get("content").asText());
        assertEquals("Second comment", response.get(1).get("content").asText());
    }

    @Test
    void shouldDeleteCommentSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");
        Long commentId = addCommentToVideo(token, videoId, "Comment to delete");

        mockMvc.perform(delete("/api/videos/" + videoId + "/comments/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify comment is deleted
        MvcResult result = mockMvc.perform(get("/api/videos/" + videoId + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode response = objectMapper.readTree(responseJson);
        assertEquals(0, response.size());
    }

    @Test
    void shouldRateVideoSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");

        ObjectNode ratingRequest = objectMapper.createObjectNode();
        ratingRequest.put("rating", 5);

        String expectedJson = """
        {
          "userRating": 5,
          "averageRating": 5.0
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/videos/" + videoId + "/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isOk())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldGetVideoRatingSuccessfully() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");
        rateVideo(token, videoId, 4);

        String expectedJson = """
        {
          "userRating": 4,
          "averageRating": 4.0
        }
        """;

        MvcResult result = mockMvc.perform(get("/api/videos/" + videoId + "/rating")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldRejectInvalidVideoData() throws Exception {
        String token = getJWTToken();

        ObjectNode request = objectMapper.createObjectNode();
        request.put("url", ""); // Empty URL
        request.put("title", ""); // Empty title

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/videos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectAccessToNonexistentVideo() throws Exception {
        String token = getJWTToken();

        String expectedJson = """
        {
          "message": "Requested resource not found",
          "status": "NOT_FOUND"
        }
        """;

        MvcResult result = mockMvc.perform(get("/api/videos/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectDeleteVideoByNonOwner() throws Exception {
        String token1 = getJWTToken();
        String token2 = getJWTTokenForAnotherUser();

        Long videoId = createTestVideo(token1, "User 1 Video", "https://example.com/user1");

        String expectedJson = """
        {
          "message": "You don't have permission to access this resource",
          "status": "FORBIDDEN"
        }
        """;

        MvcResult result = mockMvc.perform(delete("/api/videos/" + videoId)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isForbidden())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectInvalidRating() throws Exception {
        String token = getJWTToken();
        Long videoId = createTestVideo(token, "Test Video", "https://example.com/video");

        ObjectNode ratingRequest = objectMapper.createObjectNode();
        ratingRequest.put("rating", 10); // Invalid rating (should be 1-5)

        String expectedJson = """
        {
          "message": "Request contains invalid fields",
          "status": "BAD_REQUEST"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/videos/" + videoId + "/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        asserter.assertErrorResponse(result, expectedJson);
    }

    // Helper methods
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

    private Long addCommentToVideo(String token, Long videoId, String content) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("content", content);

        MvcResult result = mockMvc.perform(post("/api/videos/" + videoId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).get("id").asLong();
    }

    private void rateVideo(String token, Long videoId, int rating) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("rating", rating);

        mockMvc.perform(post("/api/videos/" + videoId + "/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private String getJWTTokenForAnotherUser() throws Exception {
        ObjectNode registerReq = objectMapper.createObjectNode();
        registerReq.put("email", "videouser2@example.com");
        registerReq.put("password", "SecurePassword123!");
        registerReq.put("name", "Video User Two");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        ObjectNode loginReq = objectMapper.createObjectNode();
        loginReq.put("email", "videouser2@example.com");
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