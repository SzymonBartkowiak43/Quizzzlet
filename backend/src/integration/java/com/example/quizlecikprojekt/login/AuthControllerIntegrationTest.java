package com.example.quizlecikprojekt.login;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.example.quizlecikprojekt.login.models.UserJsonModel;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // given
        UserRegisterDto registrationDto = new UserRegisterDto(
                "testuser@example.com", "testuser","StrongPass123!"
        );

        String expectedJson = UserJsonModel.maximal(
                1L, "testuser@example.com", "testuser", "USER"
        ).toString();

        // when
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        asserter.assertApiResponse(result, expectedJson);
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("email", "loginuser@example.com");
        loginRequest.put("password", "Password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        asserter.assertAuthResponse(loginResult, "loginuser@example.com");
    }


    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // given:
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("email", "loginuser@example.com");
        loginRequest.put("password", "WrongPassword!");

        String expectedJson = """
    {
        "message": "Bad Credentials",
        "status": "UNAUTHORIZED"
    }
    """;

        // when
        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // then
        asserter.assertErrorResponse(loginResult, expectedJson);
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        // given
        var dto = new UserRegisterDto("dupe@example.com",  "dupeUser","StrongPass123!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // when
        var dupe = new UserRegisterDto("dupe@example.com", "dupeUser2","AnotherStrong123!");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupe)))
                .andExpect(status().isConflict())
                .andReturn();

        String expectedJson = """
    {
      "message": "Email already exists",
      "status": "CONFLICT"
    }
    """;
        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {
        // given
        var dto = new UserRegisterDto("userA@example.com",  "sameName", "StrongPass123!");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // when
        var dupe = new UserRegisterDto("userB@example.com",  "sameName", "OtherStrong123!");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupe)))
                .andExpect(status().isConflict())
                .andReturn();

        String expectedJson = """
    {
      "message": "Username already exists",
      "status": "CONFLICT"
    }
    """;
        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectInvalidEmailFormat() throws Exception {
        var dto = new UserRegisterDto("not-an-email", "invalidEmailUser", "StrongPass123!");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedJson = """
    {
      "message": "Request contains invalid fields",
      "errorCode": "404",
      "violations":["email: must be a well-formed email address"]
    }
    """;
        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldRejectWeakPassword() throws Exception {
        var dto = new UserRegisterDto("weakpass@example.com",  "weak", "123");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedJson = """
{
  "message": "Password does not meet complexity requirements",
  "errorCode": "400",
  "violations": [
    "Password should contain at least one uppercase letter.",
    "Password should contain at least one lowercase letter.",
    "Password should have a minimum length of 10"
  ]
}
""";
        asserter.assertErrorResponse(result, expectedJson);
    }

    @Test
    void shouldTrimEmailAndUsername() throws Exception {
        var dto = new UserRegisterDto("  spaced@example.com  ", "  spacedName  ", "StrongPass123!" );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String expectedJson = UserJsonModel.maximal(
                1L, "spaced@example.com", "spacedName",
                "USER").toString();

        asserter.assertApiResponse(result, expectedJson);
    }


    protected void registerUser() throws Exception {
        UserRegisterDto registrationDto = new UserRegisterDto(
                "loginuser@example.com", "loginuser", "Password123!"
                );

         mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn();
    }

}