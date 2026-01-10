package com.game.find.word.KeywordQuiz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordQuizDto {
    private String text;
    private List<String> keywords;
    private String correctKeyword;
}
