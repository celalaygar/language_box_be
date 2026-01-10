package com.game.find.word.ScrambledWord.dto;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.entity.Words;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrambledWordResponseDto {
    private String id;
    private List<Words> words;
    private EnglishLevel level;
    private LocalDateTime createdAt;
    private Language language;
    private Integer count;
}
