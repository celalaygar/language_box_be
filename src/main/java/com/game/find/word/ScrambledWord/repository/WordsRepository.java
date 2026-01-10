package com.game.find.word.ScrambledWord.repository;

import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.entity.Words;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface WordsRepository extends MongoRepository<Words, String> {
    boolean existsByLanguageAndLevelAndCreatedAtBetween(
            Language language, EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Page<Words> findByLevel(EnglishLevel level, Pageable pageable);

    List<Words> findByLevel(EnglishLevel level);
    List<Words> findByWord(String word);
    List<Words> findByWordAndLanguageAndLevel(String word,Language language, EnglishLevel level);

    List<Words> findByLanguageAndLevel( Language language, EnglishLevel level);

    Words findByLanguageAndLevelAndCreatedAtBetween(
            Language language, EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
