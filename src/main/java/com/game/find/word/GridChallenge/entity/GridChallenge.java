package com.game.find.word.GridChallenge.entity;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "gridChallenges")
@CompoundIndex(name = "level_language_idx", def = "{'level': 1, 'language': 1}") // Sorgu optimizasyonu için index
public class GridChallenge {

    @Id
    private String id; // Örnek JSON'daki gibi bir Long id kullandık
    private Settings settings;
    private List<List<String>> grid;
    private List<WordPlacement> words;
    private List<String> wordList;
    private LocalDateTime createdAt;
    private EnglishLevel level;
    private Language language;


}