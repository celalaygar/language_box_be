package com.game.find.word.MatchSentence.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.find.word.MatchSentence.entity.MatchSentence;
import com.game.find.word.MatchSentence.repository.MatchSentenceRepository; // Repository'nizi kontrol edin
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSentenceLoadService {

    private final MatchSentenceRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
/*
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initVoiceMatchData() {
        try {
            log.info("Checking if Match Sentence data needs to be loaded...");

            // Eğer veritabanı boşsa yükleme yap
            if (repository.count() > 0) {
                log.info("Match Sentence database is not empty. Skipping load.");
                return;
            }

            // JSON dosyasını oku: resources/json/voiceMatch.json
            InputStream inputStream = new ClassPathResource("json/voiceMatch.json").getInputStream();
            List<MatchSentence> voiceMatchList = objectMapper.readValue(inputStream, new TypeReference<List<MatchSentence>>() {});

            log.info("Loaded {} Match Sentence items from JSON. Starting bulk save...", voiceMatchList.size());
            bulkSaveInChunks(voiceMatchList, 5000);

            log.info("Match Sentence data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to load Match Sentence data: ", e);
        }
    }
*/
    private void bulkSaveInChunks(List<MatchSentence> list, int chunkSize) {
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, list.size());
            List<MatchSentence> chunk = list.subList(i, end);

            executeBulkInsert(chunk);
            log.info("Match Sentence Load Progress: {}/{}", end, list.size());
        }
    }

    private void executeBulkInsert(List<MatchSentence> chunk) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MatchSentence.class);

        for (MatchSentence vm : chunk) {
            if (vm.getCreatedAt() == null) {
                vm.setCreatedAt(LocalDateTime.now());
            }
            vm.setSequenceNumber(null);

            bulkOps.insert(vm);
        }

        try {
            bulkOps.execute();
        } catch (Exception e) {
            log.debug("Some Match Sentence items were skipped due to duplication.");
        }
    }
}