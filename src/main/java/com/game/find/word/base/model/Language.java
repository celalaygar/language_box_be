package com.game.find.word.base.model;

public enum Language {
    EN(1, "English"),
    FR(2, "French"),
    DE(3, "German"),
    ES(4, "Spanish"),
    IT(5, "Italian"),
    PT(6, "Portuguese"),
    RU(7, "Russian"),
    KO(8, "Korean"),
    TR(9, "Turkish");

    private final int id;
    private final String description;

    Language(int id, String description) {
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