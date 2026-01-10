package com.game.find.word.googleAI.dto;


import java.util.List;

public class Content {
    private List<PromptPart> parts;

    public Content(List<PromptPart> parts) {
        this.parts = parts;
    }

    // Getters and Setters
    public List<PromptPart> getParts() {
        return parts;
    }

    public void setParts(List<PromptPart> parts) {
        this.parts = parts;
    }
}