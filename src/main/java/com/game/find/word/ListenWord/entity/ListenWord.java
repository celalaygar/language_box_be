package com.game.find.word.ListenWord.entity;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "listen_word")
@CompoundIndex(name = "sentence_completion_idx", def = "{'sentence': 1, 'answer': 1, 'language': 1, 'level': 1}", unique = true)
public class ListenWord {
    @Id
    private String id;
    private String sentence;
    private String answer;
    private String shuffledWord;
    private LocalDateTime createdAt;
    private EnglishLevel level;
    private Language language;
    private String hint;
    private Long sequenceNumber;

}