package com.game.find.word.SentenceCompletion.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.SentenceCompletion.repository.SentenceCompletionRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SentenceCompletionServiceSchedule {

    private final SentenceCompletionService sentenceService;
    private final SentenceCompletionRepository sentenceCompletionRepository;

    //@Scheduled(cron = "0 25 0 * * *") // Scheduled to run every day at 12:15 AM
    public void fetchDataFromGeminiAndSave() {
        log.info("Sentence Completion Data ingestion cron job started . " + new Date());
        for (Language language : Language.values()) {
            for (EnglishLevel level : EnglishLevel.values()) {

                // Check if data already exists for the current day and language/level
                List<SentenceCompletion> existingData = sentenceService.getTodaySentencesByLevel(language, level);

                if (!existingData.isEmpty()) {
                    log.info("Data for language {} and level {} already exists for today. Skipping ingestion.", language.name(), level.getKey());
                    continue;
                }

                // Retry mechanism for API calls
                int retryCount = 0;
                boolean success = false;
                while (retryCount < 10 && !success) { // Maximum 3 retries
                    try {
                        log.info("Attempt #{} - Fetching sentences for {} {} level... with" +
                                " {}", retryCount + 1, level.name(), level.getKey(), language.name());

                        List<SentenceCompletion> completions = sentenceService.getAndShuffleSentences(level, language);
                        sentenceCompletionRepository.saveAll(completions);
                        Thread.sleep(15000);

                        log.info("{} sentences for {} {} level successfully saved with language :" +
                                " {}.", completions.size(), level.name(), level.getKey(), language.name());
                        success = true; // Mark as successful to exit the loop
                    } catch (JsonMappingException e) {
                        retryCount++;
                        log.error("An error occurred during data ingestion for {} {} level: {} with language : {}. Retrying...",
                                level.name(), level.getKey(), e.getMessage(), language.name());
                        try {
                            // Exponential back-off for retries
                            Thread.sleep(15000L * retryCount);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        retryCount++;
                        log.error("An error occurred during data ingestion for {} {} level: {} with language : {}. Retrying...",
                                level.name(), level.getKey(), e.getMessage(), language.name());
                        try {
                            // Exponential back-off for retries
                            Thread.sleep(15000L * retryCount);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        log.info("Sentence Completion Data ingestion cron job completed. " + new Date());
    }
}