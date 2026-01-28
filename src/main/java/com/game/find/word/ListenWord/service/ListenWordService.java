package com.game.find.word.ListenWord.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.ListenWord.entity.ListenWord;
import com.game.find.word.ListenWord.repository.ListenWordRepository;
import com.game.find.word.base.model.BaseGameResponse;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.googleAI.service.GeminiService;
import com.game.find.word.googleAI.utils.WordShuffler;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class ListenWordService {
    @Value("${app.sentence.default-count:3}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;
    private final GeminiService geminiService;
    private final ListenWordRepository repository;
    /**
     * Cümle tamamlama oyun verilerini BaseGameResponse formatında döner.
     */
    public BaseGameResponse<ListenWord> getAllBySequenceNumber(Long sequenceNumber, Language language, EnglishLevel level) {
        log.info("Fetching completions starting from sequence: {}, language: {}, level: {}", sequenceNumber, language, level);

        // 1. Sayfalama ayarı (0. sayfadan başla, defaultCount kadar getir)
        Pageable limit = PageRequest.of(0, defaultCount);

        // 2. Belirli bir sıradan itibaren verileri getir
        List<ListenWord> completions = repository.findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
                language,
                level,
                sequenceNumber,
                limit
        );

        // 3. Toplam kayıt sayısını al (size alanı için)
        long totalSize = repository.countByLanguageAndLevel(language, level);

        // 4. BaseGameResponse ile sarmalayarak döndür
        return BaseGameResponse.<ListenWord>builder()
                .level(level)
                .language(language)
                .size( totalSize) // Toplam cümle sayısı
                .list(completions)     // Gelen veri listesi
                .build();
    }
    public List<ListenWord> getAndShuffleSentences(EnglishLevel level, Language language) throws JsonMappingException {
        // GeminiService'den cümleleri al
        List<ListenWord> sentences = geminiService.getListenWords(level, language);

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

    public List<ListenWord> getTodaySentencesByLevel(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<ListenWord> list = repository.findByLanguageAndLevelAndCreatedAtBetween(language, level, startOfDay, endOfDay);
        log.info("ListenWordService.getTodaySentencesByLevel " + new Date());
        return list;
    }

    public void saveAllSentences(List<ListenWord> completions) {
        completions.forEach(completion -> completion.setCreatedAt(LocalDateTime.now()));
        repository.saveAll(completions);
    }



    public List<ListenWord> getRandomSentences(Language language, EnglishLevel level, Integer count) {
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
        List<ListenWord> response = mongoTemplate.aggregate(
                aggregation,
                "sentence_completion",  // collection adı
                ListenWord.class
        ).getMappedResults();
        ;
        return response.stream().map(word -> {
            if (ObjectUtils.isEmpty(word.getHint())) {
                word.setHint("____");
            }
            return word;
        }).toList();
    }


    public List<ListenWord> findAll() {
        return repository.findAll();
    }

    public ListenWord save(ListenWord sentenceCompletion) {
        sentenceCompletion.setCreatedAt(LocalDateTime.now());
        return repository.save(sentenceCompletion);
    }

    public List<ListenWord> saveAll(List<ListenWord> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return repository.saveAll(list);
    }



    public Integer bulkSaveData(List<ListenWord> completions) {
        log.info("Starting bulk save for {} Completion List", completions.size());

        // 1. Veritabanında olmayan (unique) kelimeleri filtrele
        List<ListenWord> list = completions.stream()
                .filter(w -> !repository.existsBySentenceAndAnswerAndLanguageAndLevel(
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
        List<ListenWord> savedWords = repository.saveAll(list);
        log.info("Successfully saved {} new Completion List.", savedWords.size());

        return savedWords.size();
    }


    public Boolean reindexAllData() {
        try {
            log.info("Starting to re-index sequence numbers for all completion List...");

            for (Language lang : Language.values()) {
                for (EnglishLevel lvl : EnglishLevel.values()) {

                    List<ListenWord> list = repository.findByLanguageAndLevel(lang, lvl);
                    Collections.reverse(list);

                    if (!list.isEmpty()) {
//                        list.sort(Comparator.comparing(ListenWord::getSequenceNumber,
//                                Comparator.nullsLast(Comparator.naturalOrder())));

                        long counter = 1;
                        for (ListenWord completion : list) {
                            completion.setSequenceNumber(counter++);
                        }

                        repository.saveAll(list);
                        log.info("Re-indexed {} completion List for Language: {} and Level: {}", list.size(), lang, lvl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while re-indexing completion List: ", e);
            System.out.println("Error occurred while re-indexing completion List: "+ e);
            return false;
        }
    }
}