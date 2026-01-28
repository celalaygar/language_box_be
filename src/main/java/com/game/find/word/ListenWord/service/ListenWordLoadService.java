package com.game.find.word.ListenWord.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.find.word.ListenWord.entity.ListenWord;
import com.game.find.word.ListenWord.repository.ListenWordRepository;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListenWordLoadService {

    private final ListenWordRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initCompletionData() {
        try {
            log.info("Checking if Listen Word data needs to be loaded...");

            // Veritabanı doluluk kontrolü
            if (repository.count() > 0) {
                log.info("Listen Word database is not empty. Skipping load.");
                return;
            }

            // JSON dosyasını oku
            InputStream inputStream = new ClassPathResource("json/sentenceCompletion.json").getInputStream();
            List<ListenWord> completionList = objectMapper.readValue(inputStream, new TypeReference<List<ListenWord>>() {});

            log.info("Loaded {} completion items from JSON. Starting bulk save...", completionList.size());
            bulkSaveInChunks(completionList, 5000);

            log.info("Listen Word data initialization completed.");
        } catch (Exception e) {
            log.error("Failed to load completion data: ", e);
        }
    }

    private void bulkSaveInChunks(List<ListenWord> list, int chunkSize) {
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, list.size());
            List<ListenWord> chunk = list.subList(i, end);

            executeBulkInsert(chunk);
            log.info("Completion Load Progress: {}/{}", end, list.size());
        }
    }

    private void executeBulkInsert(List<ListenWord> chunk) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ListenWord.class);

        for (ListenWord sc : chunk) {
            if (sc.getCreatedAt() == null) {
                sc.setCreatedAt(LocalDateTime.now());
            }
            sc.setSequenceNumber(null); // Otomatik atanması isteniyorsa null set edilir

            bulkOps.insert(sc);
        }

        try {
            bulkOps.execute();
        } catch (Exception e) {
            log.debug("Some completion items skipped (potential duplicates).");
        }
    }
}