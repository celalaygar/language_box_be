package com.game.find.word.VoiceMatch.repository;

import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoiceMatchRepository extends MongoRepository<VoiceMatch, String> {
    List<VoiceMatch> findByLanguageAndLevelAndCreatedAtBetween(
            Language language,
            EnglishLevel level,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    List<VoiceMatch> findByLevelAndCreatedAtBetween(EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
