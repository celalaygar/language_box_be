package com.game.find.word.ScrambledWord.repository;

import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.entity.Words;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface WordsRepository extends MongoRepository<Words, String> {

    List<Words> findByWordAndLanguageAndLevel(String word,Language language, EnglishLevel level);

    List<Words> findByLanguageAndLevel( Language language, EnglishLevel level);
    boolean existsByWordAndLanguageAndLevel(String word, Language language, EnglishLevel level);
    // sequenceNumber >= startSeq kriterine uyan,
    // dile ve seviyeye göre filtrelenmiş verileri getirir.
    List<Words> findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
            Language language,
            EnglishLevel level,
            Long startSeq,
            Pageable pageable
    );

    Long countByLanguageAndLevel(Language language, EnglishLevel level);
}
