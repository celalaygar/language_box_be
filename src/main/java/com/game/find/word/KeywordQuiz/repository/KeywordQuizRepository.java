package com.game.find.word.KeywordQuiz.repository;

import com.game.find.word.KeywordQuiz.entity.KeywordQuiz;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface KeywordQuizRepository extends MongoRepository<KeywordQuiz, String> {
    boolean existsByLanguageAndLevelAndCreatedAtBetween(
            Language language, EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Page<KeywordQuiz> findByLevel(EnglishLevel level, Pageable pageable);


    List<KeywordQuiz> findByLevel(EnglishLevel level);


    List<KeywordQuiz> findByLanguageAndLevelAndCreatedAtBetween(
            Language language, EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
