package com.game.find.word.VoiceMatch.entity;


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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "voice_match")
@CompoundIndex(name = "voice_match_idx", def = "{'correctSentence': 1, 'language': 1, 'level': 1}", unique = true)
public class VoiceMatch {
    @Id
    private String id;
    private String correctSentence;
    private List<String> similarOptions;
    private List<String> focusWords;
    private LocalDateTime createdAt;
    private EnglishLevel level;
    private Language language;
    private Long sequenceNumber;
}
