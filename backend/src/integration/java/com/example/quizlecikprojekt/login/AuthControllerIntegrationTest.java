package com.example.quizlecikprojekt.login;

import com.example.quizlecikprojekt.Asserter;
import com.example.quizlecikprojekt.BaseIntegrationTest;
import com.example.quizlecikprojekt.domain.user.User;
import com.example.quizlecikprojekt.domain.user.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private Asserter asserter;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // given
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser@example.com",
                "StrongPass123!",
                "testuser"
        );

        User expectedUser = new User();
        expectedUser.setEmail("testuser@example.com");
        expectedUser.setUserName("testuser");

        String expectedJson = """
                        {
                          "success": true,
                          "message": "User registered successfully",
                          "data": {
                            "id": 2,
                            "email": "testuser@example.com",
                            "userName": "testuser",
                            "roles": ["USER"]
                          }
                        }
                """;

        // when
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        asserter.assertApiResponse(result, expectedJson);
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
}
