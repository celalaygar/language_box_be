package com.game.find.word.sentenceBuilder.repository;

import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SentenceBuildGameRepository extends MongoRepository<SentenceBuildGame, String> {
    List<SentenceBuildGame> findByLanguageAndLevelAndCreatedAtBetween(
            Language language,
            EnglishLevel level,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    List<SentenceBuildGame> findByLevelAndCreatedAtBetween(EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
}