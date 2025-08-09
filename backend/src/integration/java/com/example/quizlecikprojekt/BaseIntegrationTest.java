package com.example.quizlecikprojekt;

import com.example.quizlecikprojekt.domain.user.UserRole;
import com.example.quizlecikprojekt.domain.user.UserRoleRepository;
import com.example.quizlecikprojekt.domain.user.dto.UserRegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QuizlecikProjektApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseIntegrationTest {

    public static final String WIRE_MOCK_HOST = "http://localhost";

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    public Asserter asserter;

    @Autowired
    UserRoleRepository roleRepo;

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
                    .withDatabaseName("integration-tests-db")
                    .withUsername("testuser")
                    .withPassword("testpass")
                    .withCommand("postgres", "-c", "log_statement=all")
                    .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF-8 --lc-collate=C --lc-ctype=C");


    static {
        postgresContainer.start();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        roleRepo.findByName("USER")
                .orElseGet(() -> {
                    UserRole r = new UserRole();
                    r.setName("USER");
                    return roleRepo.save(r);
                });
    }

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("jobOffer.offer-fetchable.http.client.config.port", () -> wireMockServer.getPort());
        registry.add("jobOffer.offer-fetchable.http.client.config.uri", () -> WIRE_MOCK_HOST);
    }

    public static String readJsonFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/integration/resources/json/" + fileName)));
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


    protected String getJWTToken() throws Exception {
        registerUser();

        ObjectNode loginRequest = objectMapper.createObjectNode();
        loginRequest.put("email", "loginuser@example.com");
        loginRequest.put("password", "Password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/token")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        return objectMapper.readTree(jsonResponse).get("token").asText();
    }

}