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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class Asserter {

    private final ApplicationContext applicationContext;

    public Asserter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> void assertApiAndDbState(
            MvcResult apiResponse,
            String expectedJson,
            long expectedDbCount,
            T expectedDbEntity) throws Exception {

        assertApiResponse(apiResponse, expectedJson);
        assertDbMatch(expectedDbEntity, expectedDbCount);
    }

    public void assertErrorResponse(MvcResult apiResponse, String expectedErrorJson) throws Exception {
        String actual = apiResponse.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedErrorJson, actual, JSONCompareMode.NON_EXTENSIBLE);
    }

    public void assertApiResponse(MvcResult apiResponse, String expectedJson) throws Exception {
        String actualJson = apiResponse.getResponse().getContentAsString();

        if (actualJson.isBlank()) {
            assertThat(expectedJson).isEqualTo("{}");
            return;
        }

        List<String> ignoreFields = List.of("timestamp");
        Customization[] customizations = ignoreFields.stream()
                .map(field -> new Customization("**." + field, (o1, o2) -> true))
                .toArray(Customization[]::new);

        CustomComparator comparator = new CustomComparator(JSONCompareMode.STRICT, customizations);
        JSONAssert.assertEquals(expectedJson, actualJson, comparator);
    }

    @SuppressWarnings("unchecked")
    public <T> void assertDbMatch(T expectedDbEntity, long expectedDbCount) throws Exception {
        if (expectedDbEntity != null) {
            Object id = getIdValue(expectedDbEntity);
            if (id == null) {
                throw new IllegalArgumentException("Expected entity has no 'id' field or it's null");
            }

            JpaRepository<T, Object> repository = (JpaRepository<T, Object>)
                    applicationContext.getBean(getRepositoryBeanName(expectedDbEntity.getClass()));

            assertThat(repository.count()).isEqualTo(expectedDbCount);

            T entityFromDb = repository.findById(id).orElseThrow();
            assertThat(entityFromDb)
                    .usingRecursiveComparison()
                    .ignoringFieldsMatchingRegexes(".*createdAt.*", ".*updatedAt.*", ".*hibernate.*")
                    .isEqualTo(expectedDbEntity);
        }
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




    private String getRepositoryBeanName(Class<?> entityClass) {
        String simpleName = entityClass.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1) + "Repository";
    }

    private Object getIdValue(Object entity) throws Exception {
        Field idField = null;
        Class<?> clazz = entity.getClass();

        while (clazz != null) {
            try {
                idField = clazz.getDeclaredField("id");
                idField.setAccessible(true);
                return idField.get(entity);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}