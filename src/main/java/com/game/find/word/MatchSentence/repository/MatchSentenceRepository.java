package com.game.find.word.MatchSentence.repository;

import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.MatchSentence.entity.MatchSentence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchSentenceRepository extends MongoRepository<MatchSentence, String> {
    List<MatchSentence> findByLanguageAndLevelAndCreatedAtBetween(
            Language language,
            EnglishLevel level,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);
    List<MatchSentence> findByLanguageAndLevel(Language language, EnglishLevel level);

    // sequenceNumber >= startSeq kriterine uyan,
    // dile ve seviyeye göre filtrelenmiş verileri getirir.
    List<MatchSentence> findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
            Language language,
            EnglishLevel level,
            Long startSeq,
            Pageable pageable
    );
    Long countByLanguageAndLevel(Language language, EnglishLevel level);
    boolean existsByCorrectSentenceAndLanguageAndLevel(String correctSentence, Language language, EnglishLevel level);
}
