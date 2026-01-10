package com.game.find.word.ScrambledWord.repository;

import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.entity.ScrambledWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface ScrambledWordRepository extends MongoRepository<ScrambledWord, String> {
    boolean existsByLanguageAndLevelAndCreatedAtBetween(
            Language language, EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Page<ScrambledWord> findByLevel(EnglishLevel level, Pageable pageable);


    List<ScrambledWord> findByLevel(EnglishLevel level);

    List<ScrambledWord> findByLanguageAndLevel( Language language, EnglishLevel level);
    List<ScrambledWord> findByLanguageAndLevelAndRead( Language language, EnglishLevel level, Boolean read);
    @Query("{$and: [" +
            "   {'language': ?0, 'level': ?1}," + // Parametre 0 ve 1
            "   {$or: [{'read': false}, {'read': {$exists: false}}]}," + // read: false VEYA 'read' alanı yok
            "   {'words': {$exists: true, $not: {$size: 0}}}" + // 'words' alanı var VE boyutu 0 değil (yani dolu)
            "]}")
    List<ScrambledWord> findUnreadAndNonEmptyWordsByLanguageAndLevel(Language language, EnglishLevel level);
    ScrambledWord findByLanguageAndLevelAndCreatedAtBetween(
            Language language, EnglishLevel level, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
