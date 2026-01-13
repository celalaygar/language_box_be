package com.game.find.word.sentenceBuilder.repository;

import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import org.springframework.data.domain.Pageable;
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

    List<SentenceBuildGame> findByLanguageAndLevel( Language language, EnglishLevel level);
    // sequenceNumber >= startSeq kriterine uyan,
    // dile ve seviyeye göre filtrelenmiş verileri getirir.
    List<SentenceBuildGame> findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
            Language language,
            EnglishLevel level,
            Long startSeq,
            Pageable pageable
    );
    boolean existsBySentenceAndLanguageAndLevel(String sentence, Language language, EnglishLevel level);
}