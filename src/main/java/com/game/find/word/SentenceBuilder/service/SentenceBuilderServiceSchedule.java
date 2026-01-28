package com.game.find.word.SentenceBuilder.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.SentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.SentenceBuilder.entity.SentenceBuildGameDto;
import com.game.find.word.SentenceBuilder.repository.SentenceBuildGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SentenceBuilderServiceSchedule {

    private final SentenceBuilderService sentenceBuilderService;
    private final SentenceBuildGameRepository sentenceBuildGameRepository;

    //@Scheduled(cron = "0 3 0 * * *")
    public void fetchSentenceBuildGameDataAndSave() {
        log.info("Sentence building game data ingestion cron job started. " + new Date());
        for (Language language : Language.values()) {
            for (EnglishLevel level : EnglishLevel.values()) {

                List<SentenceBuildGame> existingData = sentenceBuilderService.getTodaySentencesForBuildGame(language, level);

                if (!existingData.isEmpty()) {
                    log.info("Data for language {} and level {} already exists for today. Skipping ingestion.", language.name(), level.getKey());
                    continue;
                }

                int retryCount = 0;
                boolean success = false;
                while (retryCount < 3 && !success) {
                    try {
                        log.info("Attempt #{} - Fetching sentence building game data for {} {} level... with" +
                                " {}", retryCount + 1, level.name(), level.getKey(), language.name());

                        List<SentenceBuildGameDto> list = sentenceBuilderService.getSentencesForBuildGame(level, language);

                        List<SentenceBuildGame> sentences = list.stream().map(s ->
                                SentenceBuildGame.builder()
                                        .sentence(s.getSentence())
                                        .mixedWords(s.getMixedWords())
                                        .level(level)
                                        .createdAt(LocalDateTime.now())
                                        .language(language)
                                        .build()).collect(Collectors.toList());

                        sentenceBuildGameRepository.saveAll(sentences);
                        Thread.sleep(15000);

                        log.info("{} sentence building game entries for {} {} level successfully saved with language : {}.",
                                sentences.size(), level.name(), level.getKey(), language.name());
                        success = true;
                    } catch (JsonMappingException e) {
                        retryCount++;
                        log.error("JSON mapping error for {} {} level with language {}. This is often a temporary API issue. Retrying... (Attempt {} of 3)",
                                level.name(), level.getKey(), language.name(), retryCount, e);
                        try {
                            Thread.sleep(15000L * retryCount);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        retryCount++;
                        log.error("An error occurred during data ingestion for {} {} level: {} with language : {}. Retrying...",
                                level.name(), level.getKey(), e.getMessage(), language.name());
                        try {
                            Thread.sleep(15000 * retryCount); // Her denemede bekleme süresini artır
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        log.info("Sentence building game data ingestion cron job completed. " + new Date());
    }
}