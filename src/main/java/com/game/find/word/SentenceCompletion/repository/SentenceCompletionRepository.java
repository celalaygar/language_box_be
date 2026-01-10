package com.game.find.word.SentenceCompletion.repository;


import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SentenceCompletionRepository extends MongoRepository<SentenceCompletion, String> {
    List<SentenceCompletion> findByLanguageAndLevelAndCreatedAtBetween(
            Language language,
            EnglishLevel level,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    List<SentenceCompletion> findByLevelAndCreatedAtBetween(EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
}