package com.example.quizlecikprojekt.login.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UserJsonModel {

    public static JsonObject maximal(
            Long id,
            String email,
            String name,
            String... roles
    ) {
        JsonArray rolesArray = new JsonArray();
        if (roles != null) {
            for (String role : roles) {
                rolesArray.add(role);
            }
        }

        JsonObject user = new JsonObject();
        if (id != null) user.addProperty("id", id);
        user.addProperty("email", email);
        user.addProperty("userName", name);
        user.add("roles", rolesArray);
        return user;
    }

}
