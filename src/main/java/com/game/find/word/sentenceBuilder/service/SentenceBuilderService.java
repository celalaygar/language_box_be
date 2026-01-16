package com.game.find.word.sentenceBuilder.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.SentenceCompletion.repository.SentenceCompletionRepository;
import com.game.find.word.base.model.BaseGameResponse;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.googleAI.service.GeminiService;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGameDto;
import com.game.find.word.sentenceBuilder.repository.SentenceBuildGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;


import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SentenceBuilderService {
    @Value("${app.sentence.default-count:3}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;

    private final GeminiService geminiService;
    private final SentenceBuildGameRepository sentenceBuildGameRepository;

    public BaseGameResponse<SentenceBuildGame> getAllBySequenceNumber(Long sequenceNumber, Language language, EnglishLevel level) {
        log.info("Fetching sentences starting from sequence: {}, language: {}, level: {}", sequenceNumber, language, level);

        // 1. Limitli listeyi çek (Sequence number'a göre sıralı)
        Pageable limit = PageRequest.of(0, defaultCount);
        List<SentenceBuildGame> sentenceList = sentenceBuildGameRepository.findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
                language,
                level,
                sequenceNumber,
                limit
        );

        // 2. Toplam cümle sayısını al (Response'daki "size" alanı için)
        long totalSize = sentenceBuildGameRepository.countByLanguageAndLevel(language, level);

        // 3. BaseGameResponse formatında paketle
        return BaseGameResponse.<SentenceBuildGame>builder()
                .level(level)
                .language(language)
                .size(totalSize) // Toplam cümle sayısı
                .list(sentenceList)    // Belirlenen sayıdaki (örn: 3 adet) liste
                .build();
    }


    public List<SentenceBuildGame> getTodaySentencesForBuildGame(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<SentenceBuildGame> list = sentenceBuildGameRepository.findByLanguageAndLevelAndCreatedAtBetween(
                language, level, startOfDay, endOfDay);

        log.info("SentenceBuilderService.getTodaySentencesForBuildGame " + new Date());
        return list;
    }


    public List<SentenceBuildGameDto> getSentencesForBuildGame(EnglishLevel level, Language language) throws JsonMappingException {
        List<SentenceBuildGameDto> sentences = geminiService.getSentencesForBuildGame(level, language);
        return sentences;
    }

    public List<SentenceBuildGame> getRandomSentences(Language language, EnglishLevel level, Integer count) {
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

        return mongoTemplate.aggregate(
                aggregation,
                "sentence_build", // collection adı
                SentenceBuildGame.class
        ).getMappedResults();
    }


    public List<SentenceBuildGame> findAll() {
        return sentenceBuildGameRepository.findAll();
    }

    public SentenceBuildGame save(SentenceBuildGame game) {
        game.setCreatedAt(LocalDateTime.now());
        return sentenceBuildGameRepository.save(game);
    }

    public List<SentenceBuildGame> saveAll(List<SentenceBuildGame> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return sentenceBuildGameRepository.saveAll(list);
    }

    public Integer bulkSaveData(List<SentenceBuildGame> gameList) {
        log.info("Starting bulk save for {} game List", gameList.size());

        // 1. Veritabanında olmayan (unique) kelimeleri filtrele
        List<SentenceBuildGame> gameListToSave = gameList.stream()
                .filter(w -> !sentenceBuildGameRepository.existsBySentenceAndLanguageAndLevel(
                        w.getSentence(),
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

        if (gameListToSave.isEmpty()) {
            log.warn("No new unique words found to save.");
            return 0;
        }

        // 2. Toplu kaydet
        List<SentenceBuildGame> savedWords = sentenceBuildGameRepository.saveAll(gameListToSave);
        log.info("Successfully saved {} new game List.", savedWords.size());

        return savedWords.size();
    }


    public Boolean reindexAllData() {
        try {
            log.info("Starting to re-index sequence numbers for all game List...");

            for (Language lang : Language.values()) {
                for (EnglishLevel lvl : EnglishLevel.values()) {

                    List<SentenceBuildGame> list = sentenceBuildGameRepository.findByLanguageAndLevel(lang, lvl);

                    if (!list.isEmpty()) {
                        list.sort(Comparator.comparing(SentenceBuildGame::getSequenceNumber,
                                Comparator.nullsLast(Comparator.naturalOrder())));

                        long counter = 1;
                        for (SentenceBuildGame game : list) {
                            game.setSequenceNumber(counter++);
                        }

                        sentenceBuildGameRepository.saveAll(list);
                        log.info("Re-indexed {} game List for Language: {} and Level: {}", list.size(), lang, lvl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while re-indexing game List: ", e);
            return false;
        }
    }
}