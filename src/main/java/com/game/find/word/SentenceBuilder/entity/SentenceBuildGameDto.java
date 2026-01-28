package com.game.find.word.SentenceBuilder.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentenceBuildGameDto {
    @JsonProperty("sentence")
    private String sentence;
    @JsonProperty("mixedWords")
    private List<String> mixedWords;

    private Long sequenceNumber;

}