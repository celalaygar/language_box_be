package com.game.find.word.VoiceMatch.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.VoiceMatch.repository.VoiceMatchRepository; // Repository'nizi kontrol edin
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
public class VoiceMatchLoadService {

    private final VoiceMatchRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initVoiceMatchData() {
        try {
            log.info("Checking if voice match data needs to be loaded...");

            // Eğer veritabanı boşsa yükleme yap
            if (repository.count() > 0) {
                log.info("Voice match database is not empty. Skipping load.");
                return;
            }

            // JSON dosyasını oku: resources/json/voiceMatch.json
            InputStream inputStream = new ClassPathResource("json/voiceMatch.json").getInputStream();
            List<VoiceMatch> voiceMatchList = objectMapper.readValue(inputStream, new TypeReference<List<VoiceMatch>>() {});

            log.info("Loaded {} voice match items from JSON. Starting bulk save...", voiceMatchList.size());
            bulkSaveInChunks(voiceMatchList, 5000);

            log.info("Voice match data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to load voice match data: ", e);
        }
    }

    private void bulkSaveInChunks(List<VoiceMatch> list, int chunkSize) {
        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, list.size());
            List<VoiceMatch> chunk = list.subList(i, end);

            executeBulkInsert(chunk);
            log.info("Voice Match Load Progress: {}/{}", end, list.size());
        }
    }

    private void executeBulkInsert(List<VoiceMatch> chunk) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VoiceMatch.class);

        for (VoiceMatch vm : chunk) {
            if (vm.getCreatedAt() == null) {
                vm.setCreatedAt(LocalDateTime.now());
            }
            vm.setSequenceNumber(null);

            bulkOps.insert(vm);
        }

        try {
            bulkOps.execute();
        } catch (Exception e) {
            log.debug("Some voice match items were skipped due to duplication.");
        }
    }
}