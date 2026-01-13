package com.game.find.word.ScrambledWord.service;


import com.game.find.word.ScrambledWord.dto.ScrambledWordResponseDto;
import com.game.find.word.ScrambledWord.entity.*;
import com.game.find.word.ScrambledWord.model.Word;
import com.game.find.word.ScrambledWord.repository.ScrambledWordRepository;
import com.game.find.word.ScrambledWord.repository.WordsRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
@Slf4j
public class WordsService {

    @Value("${app.words.default-count:20}")
    private int defaultCount;
    private final ScrambledWordRepository scrambledWordRepository;
    private final WordsRepository repository;
    private final MongoTemplate mongoTemplate;


    /**
     * Belirli bir başlangıç sequenceNumber'ından itibaren
     * defaultCount kadar kelimeyi liste olarak döner.
     */
    public List<Words> getAllBySequenceNumber(Long sequenceNumber, Language language, EnglishLevel level) {
        log.info("Fetching words starting from sequence: {}, language: {}, level: {}", sequenceNumber, language, level);

        // Limit belirlemek için PageRequest kullanıyoruz (0. sayfa, defaultCount kadar kayıt)
        Pageable limit = PageRequest.of(0, defaultCount);

        return repository.findByLanguageAndLevelAndSequenceNumberGreaterThanEqualOrderBySequenceNumberAsc(
                language,
                level,
                sequenceNumber,
                limit
        );
    }

    public Boolean reindexAllData() {
        try {
            log.info("Starting to re-index sequence numbers for all words...");

            for (Language lang : Language.values()) {
                for (EnglishLevel lvl : EnglishLevel.values()) {

                    List<Words> wordsList = repository.findByLanguageAndLevel(lang, lvl);

                    if (!wordsList.isEmpty()) {
                        wordsList.sort(Comparator.comparing(Words::getSequenceNumber,
                                Comparator.nullsLast(Comparator.naturalOrder())));

                        long counter = 1;
                        for (Words word : wordsList) {
                            word.setSequenceNumber(counter++);
                        }

                        repository.saveAll(wordsList);
                        log.info("Re-indexed {} words for Language: {} and Level: {}", wordsList.size(), lang, lvl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while re-indexing words: ", e);
            return false;
        }
    }
    /**
     * Listeyi kontrol ederek (Duplicate Check) toplu kayıt yapar.
     * sequenceNumber alanını boş bırakır.
     * @param wordsList Kaydedilmek istenen kelime listesi
     * @return Başarıyla kaydedilen kelime sayısı
     */
    public Integer bulkSaveData(List<Words> wordsList) {
        log.info("Starting bulk save for {} words", wordsList.size());

        // 1. Veritabanında olmayan (unique) kelimeleri filtrele
        List<Words> wordsToSave = wordsList.stream()
                .filter(w -> !repository.existsByWordAndLanguageAndLevel(
                        w.getWord(),
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

        if (wordsToSave.isEmpty()) {
            log.warn("No new unique words found to save.");
            return 0;
        }

        // 2. Toplu kaydet
        List<Words> savedWords = repository.saveAll(wordsToSave);
        log.info("Successfully saved {} new words.", savedWords.size());

        return savedWords.size();
    }

    public String shuffleWord(String word) {
        List<Character> characters = word.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        characters.forEach(sb::append);
        return sb.toString();
    }

    public Set<String> findAll(Language language, EnglishLevel level) {

        List<Words> list = repository.findByLanguageAndLevel(language, level);
        Set<String> response = list.stream().map(item -> item.getWord()).collect(Collectors.toSet());
        return response;
    }

}
