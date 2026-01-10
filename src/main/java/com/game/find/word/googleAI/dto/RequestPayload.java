package com.game.find.word.googleAI.dto;


import java.util.List;

public class RequestPayload {
    private List<Content> contents;

    public RequestPayload(List<Content> contents) {
        this.contents = contents;
    }

    // Getters and Setters
    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}