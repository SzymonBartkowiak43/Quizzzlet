package com.example.quizlecikprojekt.login;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
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
        UserRegistrationDto registrationDto = createRegistrationDto(
                "testuser@example.com", "StrongPass123!", "testuser"
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
        var dto = createRegistrationDto("dupe@example.com", "StrongPass123!", "dupeUser");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // when
        var dupe = createRegistrationDto("dupe@example.com", "AnotherStrong123!", "dupeUser2");
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
        var dto = createRegistrationDto("userA@example.com", "StrongPass123!", "sameName");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // when
        var dupe = createRegistrationDto("userB@example.com", "OtherStrong123!", "sameName");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupe)))
                .andExpect(status().isConflict()) // or BAD_REQUEST
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
        var dto = createRegistrationDto("not-an-email", "StrongPass123!", "invalidEmailUser");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // if you return 400 for validation errors
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
        var dto = createRegistrationDto("weakpass@example.com", "123", "weak");

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
        var dto = createRegistrationDto("  spaced@example.com  ", "StrongPass123!", "  spacedName  ");

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

    private UserRegistrationDto createRegistrationDto(String email, String password, String username) {
        return new UserRegistrationDto(email, password, username);
    }

    private void registerUser() throws Exception {
        UserRegistrationDto registrationDto = createRegistrationDto(
                "loginuser@example.com", "Password123!", "loginuser"
        );

         mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn();
    }

}