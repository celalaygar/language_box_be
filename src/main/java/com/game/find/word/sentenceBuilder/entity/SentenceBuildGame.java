package com.game.find.word.sentenceBuilder.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "sentence_build")
@CompoundIndex(name = "word_lang_level_idx", def = "{'sentence': 1, 'language': 1, 'level': 1}", unique = true)
public class SentenceBuildGame {
    @Id
    private String id;
    @JsonProperty("sentence")
    private String sentence;
    @JsonProperty("mixedWords")
    private List<String> mixedWords;
    private LocalDateTime createdAt;
    private EnglishLevel level;
    private Language language;
    private Long sequenceNumber;


    public SentenceBuildGame(SentenceBuildGameDto dto,EnglishLevel level){
        this.sentence = dto.getSentence();
        this.mixedWords = dto.getMixedWords();
        this.level = level;
        this.createdAt = LocalDateTime.now();
        this.sequenceNumber = dto.getSequenceNumber();
    }

}