package com.example.quizlecikprojekt.login;

import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import com.example.quizlecikprojekt.newweb.dto.login.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser",
                "testuser@example.com",
                "StrongPass123!"
        );

        given()
                .contentType(ContentType.JSON)
                .body(registrationDto)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("message", equalTo("User registered successfully"))
                .body("data", equalTo("User created"));

        // DB assertion: user exists in DB
        assertThatUserExistsInDb("testuser@example.com");
    }

    @Test
    void shouldLoginUserSuccessfully() {
        // First register user
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "loginuser",
                "login@example.com",
                "Password123!"
        );
        given()
                .contentType(ContentType.JSON)
                .body(registrationDto)
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Then login
        LoginRequest loginRequest = new LoginRequest(
                "loginuser",
                "Password123!"
        );

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("message", equalTo("Login successful"))
                .body("data.username", equalTo("loginuser"));
    }

    private void assertThatUserExistsInDb(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email
        );
        org.assertj.core.api.Assertions.assertThat(count).isEqualTo(1);
    }
}
