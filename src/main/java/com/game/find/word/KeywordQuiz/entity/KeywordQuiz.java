package com.game.find.word.KeywordQuiz.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "keyword_quiz")
public class KeywordQuiz {
    @Id
    private String id;
    private String text;
    private List<String> keywords;
    private String correctKeyword;
    private LocalDateTime createdAt;
    private EnglishLevel level;
    private Language language;
}
