package com.game.find.word.MatchSentence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchSentenceDto {

    @JsonProperty("correct_sentence")
    private String correctSentence;

    @JsonProperty("similar_options")
    private List<String> similarOptions;

    @JsonProperty("focus_words")
    private List<String> focusWords;
}