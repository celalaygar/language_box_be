package com.game.find.word.googleAI.dto;


public class PromptPart {
    private String text;

    public PromptPart(String text) {
        this.text = text;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}