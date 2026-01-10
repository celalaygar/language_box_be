package com.game.find.word.googleAI.entity;

import com.game.find.word.auth.dto.UserResponseStatus;

public enum ApiKeyType {

    GEMINI(0, "GEMINI"),
    DEEPSEEK_V_3_1(1, "DEEPSEEK V 3_1");

    private final int id;
    private final String description;

    ApiKeyType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

}
