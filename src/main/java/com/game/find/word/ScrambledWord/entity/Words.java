package com.game.find.word.ScrambledWord.entity;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "words")
public class Words {
    @Id
    private String id;
    private String word;
    private String shuffledWord;
    private String hint;
    private EnglishLevel level;
    private LocalDateTime createdAt;
    private Language language;
    private Long sequenceNumber;
}
