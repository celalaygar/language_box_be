package com.game.find.word.ScrambledWord.entity;


import com.game.find.word.ScrambledWord.model.Word;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "scrambled_word")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrambledWord {
    @Id
    private String id;
    private List<Word> words;
    private EnglishLevel level;
    private LocalDateTime createdAt;
    private Language language;
    private Integer count;
    private Boolean read;
}
