package com.game.find.word.SentenceCompletion.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.SentenceCompletion.repository.SentenceCompletionRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.base.model.Language;
import com.game.find.word.googleAI.service.GeminiService;
import com.game.find.word.googleAI.utils.WordShuffler;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
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
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class SentenceCompletionService {
    @Value("${app.sentence.default-count:3}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;
    private final GeminiService geminiService;
    private final SentenceCompletionRepository sentenceCompletionRepository;

    public List<SentenceCompletion> getAllBySequenceNumber(Long sequenceNumber, Language language, EnglishLevel level) {
        log.info("Fetching words starting from sequence: {}, language: {}, level: {}", sequenceNumber, language, level);

        Pageable limit = PageRequest.of(0, defaultCount);

        return sentenceCompletionRepository.findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
                language,
                level,
                sequenceNumber,
                limit
        );
    }
    public List<SentenceCompletion> getAndShuffleSentences(EnglishLevel level, Language language) throws JsonMappingException {
        // GeminiService'den cümleleri al
        List<SentenceCompletion> sentences = geminiService.getSentenceCompletions(level, language);

        // Her bir cümlenin 'answer' kelimesini karıştır ve 'shuffledWord' değişkenine ata
        return sentences.stream()
                .map(sentenceCompletion -> {
                    String shuffled = WordShuffler.shuffleWord(sentenceCompletion.getAnswer());
                    sentenceCompletion.setShuffledWord(shuffled);
                    sentenceCompletion.setCreatedAt(LocalDateTime.now());
                    sentenceCompletion.setLanguage(language);
                    sentenceCompletion.setLevel(level);
                    return sentenceCompletion;
                })
                .collect(Collectors.toList());
    }

    public List<SentenceCompletion> getTodaySentencesByLevel(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<SentenceCompletion> list = sentenceCompletionRepository.findByLanguageAndLevelAndCreatedAtBetween(language, level, startOfDay, endOfDay);
        log.info("SentenceService.getTodaySentencesByLevel " + new Date());
        return list;
    }

    public void saveAllSentences(List<SentenceCompletion> completions) {
        completions.forEach(completion -> completion.setCreatedAt(LocalDateTime.now()));
        sentenceCompletionRepository.saveAll(completions);
    }



    public List<SentenceCompletion> getRandomSentences(Language language, EnglishLevel level, Integer count) {
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
        List<SentenceCompletion> response = mongoTemplate.aggregate(
                aggregation,
                "sentence_completion",  // collection adı
                SentenceCompletion.class
        ).getMappedResults();
        ;
        return response.stream().map(word -> {
            if (ObjectUtils.isEmpty(word.getHint())) {
                word.setHint("____");
            }
            return word;
        }).toList();
    }


    public List<SentenceCompletion> findAll() {
        return sentenceCompletionRepository.findAll();
    }

    public SentenceCompletion save(SentenceCompletion sentenceCompletion) {
        sentenceCompletion.setCreatedAt(LocalDateTime.now());
        return sentenceCompletionRepository.save(sentenceCompletion);
    }

    public List<SentenceCompletion> saveAll(List<SentenceCompletion> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return sentenceCompletionRepository.saveAll(list);
    }



    public Integer bulkSaveData(List<SentenceCompletion> completions) {
        log.info("Starting bulk save for {} Completion List", completions.size());

        // 1. Veritabanında olmayan (unique) kelimeleri filtrele
        List<SentenceCompletion> list = completions.stream()
                .filter(w -> !sentenceCompletionRepository.existsBySentenceAndAnswerAndLanguageAndLevel(
                        w.getSentence(),
                        w.getAnswer(),
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
        List<SentenceCompletion> savedWords = sentenceCompletionRepository.saveAll(list);
        log.info("Successfully saved {} new Completion List.", savedWords.size());

        return savedWords.size();
    }


    public Boolean reindexAllData() {
        try {
            log.info("Starting to re-index sequence numbers for all completion List...");

            for (Language lang : Language.values()) {
                for (EnglishLevel lvl : EnglishLevel.values()) {

                    List<SentenceCompletion> list = sentenceCompletionRepository.findByLanguageAndLevel(lang, lvl);

                    if (!list.isEmpty()) {
                        list.sort(Comparator.comparing(SentenceCompletion::getSequenceNumber,
                                Comparator.nullsLast(Comparator.naturalOrder())));

                        long counter = 1;
                        for (SentenceCompletion completion : list) {
                            completion.setSequenceNumber(counter++);
                        }

                        sentenceCompletionRepository.saveAll(list);
                        log.info("Re-indexed {} completion List for Language: {} and Level: {}", list.size(), lang, lvl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while re-indexing completion List: ", e);
            return false;
        }
    }
}