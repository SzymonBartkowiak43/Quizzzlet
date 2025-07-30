package com.example.quizlecikprojekt.login;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser@example.com",
                "StrongPass123!",
                "testuser"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data").value("User created"));

        // DB assertion
        assertThatUserExistsInDb("testuser@example.com");
    }

//    @Test
//    void shouldLoginUserSuccessfully() {
//        // First register user
//        UserRegistrationDto registrationDto = new UserRegistrationDto(
//                "loginuser",
//                "login@example.com",
//                "Password123!"
//        );
//        given()
//                .contentType(ContentType.JSON)
//                .body(registrationDto)
//                .post("/api/auth/register")
//                .then()
//                .statusCode(HttpStatus.CREATED.value());
//
//        // Then login
//        LoginRequest loginRequest = new LoginRequest(
//                "loginuser",
//                "Password123!"
//        );
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(loginRequest)
//                .when()
//                .post("/api/auth/login")
//                .then()
//                .statusCode(HttpStatus.OK.value())
//                .body("message", equalTo("Login successful"))
//                .body("data.username", equalTo("loginuser"));
//    }

    private void assertThatUserExistsInDb(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email
        );
        org.assertj.core.api.Assertions.assertThat(count).isEqualTo(1);
    }
}
