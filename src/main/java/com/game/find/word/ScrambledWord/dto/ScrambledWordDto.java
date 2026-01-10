package com.game.find.word.ScrambledWord.dto;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrambledWordDto {
    private String shuffledWord;
    private EnglishLevel level;
    private List<String> words;
    private LocalDateTime createdAt;
    private Language language;
}
