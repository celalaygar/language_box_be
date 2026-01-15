package com.game.find.word.ScrambledWord.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.ScrambledWord.repository.ScrambledWordRepository;
import com.game.find.word.ScrambledWord.repository.WordsRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordsLoadService {

    @Value("${app.words.default-count:20}")
    private int defaultCount;
    private final ScrambledWordRepository scrambledWordRepository;
    private final WordsRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    @EventListener(ApplicationReadyEvent.class) // Uygulama tamamen hazır olduğunda çalışır
    @Async // İsteğe bağlı: Ana thread'i kilitlememek için (@EnableAsync eklemeyi unutma)
    public void initData() {
        try {
            log.info("Checking if word data needs to be loaded...");

            // 270 bin kayıt varken her seferinde baştan başlamasın diye kontrol ekle
            if (repository.count() > 0) {
                log.info("Database is not empty. Skipping data load.");
                return;
            }

            InputStream inputStream = new ClassPathResource("json/word.json").getInputStream();

            // Jackson'ın devasa dosyayı okurken hata vermemesi için buffer kullanabilirsin
            List<Words> wordsList = objectMapper.readValue(inputStream, new TypeReference<List<Words>>() {});

            log.info("Loaded {} words from JSON. Starting bulk save...", wordsList.size());
            bulkSaveDataInChunks(wordsList, 5000);

        } catch (Exception e) {
            log.error("Failed to load initial data: ", e);
        }
    }

    /**
     * Veriyi parçalara bölerek BulkOperations ile çok hızlı kaydeder.
     */
    public void bulkSaveDataInChunks(List<Words> wordsList, int chunkSize) {
        for (int i = 0; i < wordsList.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, wordsList.size());
            List<Words> chunk = wordsList.subList(i, end);

            executeBulkInsert(chunk);
            log.info("Progress: {}/{}", end, wordsList.size());
        }
    }

    private void executeBulkInsert(List<Words> chunk) {
        // Unordered: Bir kayıtta hata (duplicate vb.) olsa bile diğerlerini kaydetmeye devam eder.
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Words.class);

        for (Words w : chunk) {
            w.setSequenceNumber(null);
            if (w.getCreatedAt() == null) w.setCreatedAt(LocalDateTime.now());

            // Önceki adımda konuştuğumuz CompoundIndex (unique=true) varsa:
            // insert() metodu duplicate hatasında o satırı geçer, geri kalanı yazar.
            bulkOps.insert(w);
        }

        try {
            bulkOps.execute();
        } catch (Exception e) {
            // Duplicate key hatalarını burada loglayabilir veya görmezden gelebilirsiniz
            log.debug("Some records were skipped due to duplication.");
        }
    }

}
