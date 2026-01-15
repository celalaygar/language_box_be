package com.game.find.word.sentenceBuilder.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.ScrambledWord.repository.ScrambledWordRepository;
import com.game.find.word.ScrambledWord.repository.WordsRepository;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.sentenceBuilder.repository.SentenceBuildGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class SentenceBuildLoadService {

    private final SentenceBuildGameRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    @Async // Arka planda çalışması için (Ana sınıfınızda @EnableAsync olmalı)
    public void initSentenceData() {
        try {
            log.info("Checking if sentence builder data needs to be loaded...");

            // Eğer veritabanı boşsa yükleme yap
            if (repository.count() > 0) {
                log.info("Sentence database is not empty. Skipping data load.");
                return;
            }

            // Dosyayı oku: resources/json/sentenceBuilder.json
            InputStream inputStream = new ClassPathResource("json/sentenceBuilder.json").getInputStream();
            List<SentenceBuildGame> sentenceList = objectMapper.readValue(inputStream, new TypeReference<List<SentenceBuildGame>>() {});

            log.info("Loaded {} sentences from JSON. Starting bulk save...", sentenceList.size());
            bulkSaveSentencesInChunks(sentenceList, 5000);

            log.info("Sentence data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to load initial sentence data: ", e);
        }
    }

    /**
     * Cümle verilerini paketler halinde kaydeder.
     */
    public void bulkSaveSentencesInChunks(List<SentenceBuildGame> list, int chunkSize) {
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, list.size());
            List<SentenceBuildGame> chunk = list.subList(i, end);

            executeBulkInsert(chunk);
            log.info("Sentence Load Progress: {}/{}", end, list.size());
        }
    }

    private void executeBulkInsert(List<SentenceBuildGame> chunk) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SentenceBuildGame.class);

        for (SentenceBuildGame s : chunk) {
            // Sequence logic
            if (s.getCreatedAt() == null) {
                s.setCreatedAt(LocalDateTime.now());
            }

            // Veritabanı seviyesinde unique index varsa (sentence+language+level gibi)
            // insert işlemi duplicate olanları atlayıp devam edecektir.
            bulkOps.insert(s);
        }

        try {
            bulkOps.execute();
        } catch (Exception e) {
            log.debug("Some sentences were skipped due to duplication.");
        }
    }
}