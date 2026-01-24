package com.game.find.word.MatchSentence.service;


import com.game.find.word.base.model.BaseGameResponse;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.MatchSentence.entity.MatchSentence;
import com.game.find.word.MatchSentence.repository.MatchSentenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSentenceService {
    @Value("${app.sentence.default-count:3}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;

    private final MatchSentenceRepository repository;

    /**
     * Cümle eşleştirme verilerini BaseGameResponse formatında döner.
     */
    public BaseGameResponse<MatchSentence> getAllBySequenceNumber(Long sequenceNumber, Language language, EnglishLevel level) {
        log.info("Fetching matching sentences starting from sequence: {}, language: {}, level: {}", sequenceNumber, language, level);

        // 1. Limitli veri setini getir (Sequence sırasına göre)
        Pageable limit = PageRequest.of(0, defaultCount);
        List<MatchSentence> matchSentences = repository.findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
                language,
                level,
                sequenceNumber,
                limit
        );

        // 2. İlgili filtredeki toplam kayıt sayısını al (Response'daki "size" alanı)
        long totalSize = repository.countByLanguageAndLevel(language, level);

        // 3. BaseGameResponse ile sarmalayarak döndür
        return BaseGameResponse.<MatchSentence>builder()
                .level(level)
                .language(language)
                .size(totalSize) // Toplam cümle sayısı
                .list(matchSentences)   // Çekilen veri listesi
                .build();
    }
    public List<MatchSentence> getToday(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<MatchSentence> list = repository.findByLanguageAndLevelAndCreatedAtBetween(
                language, level, startOfDay, endOfDay);

        log.info("VoiceMatchService.getTodayWordSearchGame " + new Date());
        return list;
    }

    public List<MatchSentence> getRandom(Language language, EnglishLevel level, Integer count) {
        if (count == null || count <= 0) {
            count = defaultCount;
        }

        MatchOperation matchStage = match(
                org.springframework.data.mongodb.core.query.Criteria
                        .where("language").is(language)
                        .and("level").is(level)
        );

        SampleOperation sampleStage = sample(count);

        Aggregation aggregation = newAggregation(matchStage, sampleStage);
        List<MatchSentence> list = mongoTemplate.aggregate(
                aggregation,
                "voice_match", // collection adı
                MatchSentence.class
        ).getMappedResults();
        // her VoiceMatch içindeki similarOptions listesini rastgele sırala
        for (MatchSentence vm : list) {
            if (vm.getSimilarOptions() != null && !vm.getSimilarOptions().isEmpty()) {
                Collections.shuffle(vm.getSimilarOptions());
            }
        }
        return list;
    }


    public List<MatchSentence> findAll() {
        return repository.findAll();
    }

    public MatchSentence save(MatchSentence game) {
        game.setCreatedAt(LocalDateTime.now());
        return repository.save(game);
    }

    public List<MatchSentence> saveAll(List<MatchSentence> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return repository.saveAll(list);
    }

    public Integer bulkSaveData(List<MatchSentence> voiceMatches) {
        log.info("Starting bulk save for {} Match Sentence List", voiceMatches.size());

        // 1. Veritabanında olmayan (unique) kelimeleri filtrele
        List<MatchSentence> list = voiceMatches.stream()
                .filter(w -> !repository.existsByCorrectSentenceAndLanguageAndLevel(
                        w.getCorrectSentence(),
                        w.getLanguage(),
                        w.getLevel()))
                .peek(w -> {
                    // sequenceNumber'ı garanti altına almak için null set ediyoruz
                    w.setSequenceNumber(null);
                    // Oluşturulma tarihini set et (Eğer modelde @CreatedDate yoksa)
                    if (w.getCreatedAt() == null) {
                        w.setCreatedAt(LocalDateTime.now());
                    }
                })
                .toList();

        if (list.isEmpty()) {
            log.warn("No new unique words found to save.");
            return 0;
        }

        // 2. Toplu kaydet
        List<MatchSentence> savedWords = repository.saveAll(list);
        log.info("Successfully saved {} new Match Sentence List.", savedWords.size());

        return savedWords.size();
    }


    public Boolean reindexAllData() {
        try {
            log.info("Starting to re-index sequence numbers for all Match Sentence List...");

            for (Language lang : Language.values()) {
                for (EnglishLevel lvl : EnglishLevel.values()) {

                    List<MatchSentence> list = repository.findByLanguageAndLevel(lang, lvl);

                    if (!list.isEmpty()) {
                        list.sort(Comparator.comparing(MatchSentence::getSequenceNumber,
                                Comparator.nullsLast(Comparator.naturalOrder())));

                        long counter = 1;
                        for (MatchSentence voiceMatch : list) {
                            voiceMatch.setSequenceNumber(counter++);
                        }

                        repository.saveAll(list);
                        log.info("Re-indexed {} Match Sentence List for Language: {} and Level: {}", list.size(), lang, lvl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while re-indexing Match Sentence List: ", e);
            return false;
        }
    }
}