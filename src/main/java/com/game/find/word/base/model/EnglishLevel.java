package com.game.find.word.base.model;

public enum EnglishLevel {
    A1(1, "beginner"),
    A2(2, "elementary"),
    B1(3, "intermediate"),
    B2(4, "upper-intermediate"),
    C1(5, "advanced"),
    C2(6, "proficiency");

    private final int id;
    private final String key;

    EnglishLevel(int id, String key) {
        this.id = id;
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public static EnglishLevel fromId(int id) {
        for (EnglishLevel level : EnglishLevel.values()) {
            if (level.getId() == id) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid EnglishLevel id: " + id);
    }
}