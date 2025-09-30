package com.example.quizlecikprojekt;

import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class Asserter {

    public void assertErrorResponse(MvcResult apiResponse, String expectedErrorJson) throws Exception {
        String actual = apiResponse.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedErrorJson, actual, JSONCompareMode.NON_EXTENSIBLE);
    }

    public void assertApiResponse(MvcResult apiResponse, String expectedJson) throws Exception {
        String actualJson = apiResponse.getResponse().getContentAsString(StandardCharsets.UTF_8);

        if (actualJson.isBlank()) {
            assertThat(expectedJson).isEqualTo("{}");
            return;
        }

        List<String> ignoreFields = List.of("timestamp", "createdAt", "updatedAt","completedAt", "sessionId");
        Customization[] customizations = ignoreFields.stream()
                .map(field -> new Customization("**." + field, (o1, o2) -> true))
                .toArray(Customization[]::new);

        CustomComparator comparator = new CustomComparator(JSONCompareMode.STRICT, customizations);
        JSONAssert.assertEquals(expectedJson, actualJson, comparator);
    }

    public void assertAuthResponse(MvcResult apiResponse, String expectedEmail) throws Exception {
        String actualJson = apiResponse.getResponse().getContentAsString();

        assertThat(actualJson)
                .as("Response body should not be blank")
                .isNotBlank();

        JSONObject actual = new JSONObject(actualJson);

        assertThat(actual.optString("email"))
                .as("Email in response should match expected")
                .isEqualTo(expectedEmail);

        assertThat(actual.has("token"))
                .as("Token should be present in response")
                .isTrue();
        assertThat(actual.optString("token"))
                .as("Token should not be blank")
                .isNotBlank();
    }

}