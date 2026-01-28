package com.game.find.word.ListenWord.repository;


import com.game.find.word.ListenWord.entity.ListenWord;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListenWordRepository extends MongoRepository<ListenWord, String> {
    List<ListenWord> findByLanguageAndLevelAndCreatedAtBetween(
            Language language,
            EnglishLevel level,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    List<ListenWord> findByLanguageAndLevel(Language language, EnglishLevel level);
    // sequenceNumber >= startSeq kriterine uyan,
    // dile ve seviyeye göre filtrelenmiş verileri getirir.
    List<ListenWord> findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
            Language language,
            EnglishLevel level,
            Long startSeq,
            Pageable pageable
    );
    Long countByLanguageAndLevel(Language language, EnglishLevel level);
    boolean existsBySentenceAndAnswerAndLanguageAndLevel(String sentence, String answer, Language language, EnglishLevel level);
}