package com.example.quizlecikprojekt.login.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UserJsonModel {
    public static JsonObject maximal(
            Long id,
            String email,
            String userName,
            String... roles
    ) {
        JsonArray rolesArray = new JsonArray();
        for (String role : roles) {
            rolesArray.add(role);
        }

        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("email", email);
        data.addProperty("userName", userName);
        data.add("roles", rolesArray);

        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.addProperty("message", "User registered successfully");
        response.add("data", data);

        return response;
    }
}
