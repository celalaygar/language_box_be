package com.game.find.word.SentenceCompletion.repository;


import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import org.springframework.data.domain.Pageable;
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

    List<SentenceCompletion> findByLanguageAndLevel( Language language, EnglishLevel level);
    // sequenceNumber >= startSeq kriterine uyan,
    // dile ve seviyeye göre filtrelenmiş verileri getirir.
    List<SentenceCompletion> findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
            Language language,
            EnglishLevel level,
            Long startSeq,
            Pageable pageable
    );
    Long countByLanguageAndLevel(Language language, EnglishLevel level);
    boolean existsBySentenceAndAnswerAndLanguageAndLevel(String sentence, String answer, Language language, EnglishLevel level);
}