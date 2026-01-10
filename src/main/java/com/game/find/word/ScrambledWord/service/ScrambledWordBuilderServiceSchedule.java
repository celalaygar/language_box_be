package com.game.find.word.ScrambledWord.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.entity.ScrambledWord;
import com.game.find.word.ScrambledWord.model.Word;
import com.game.find.word.ScrambledWord.repository.ScrambledWordRepository;
import com.game.find.word.googleAI.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrambledWordBuilderServiceSchedule {

    private final ScrambledWordRepository wordRepository;
    private final ScrambledWordService wordService;
    private final GeminiService geminiService;


    //@Scheduled(cron = "0 15 * * * *")
    public void fetchAndSaveWords() {
        log.info("Word ingestion cron job started. Current Time: {}", new Date());

        for (Language language : Language.values()) {
            for (EnglishLevel level : EnglishLevel.values()) {

                int retryCount = 0;
                boolean success = false;
                while (retryCount < 3 && !success) {
                    try {
                        log.info("Attempt #{} - Fetching words for level {} {} with language {}.",
                                retryCount + 1, level.name(), level.getKey(), language.name());

                        List<String> wordsFromApi = geminiService.getEnglishWords(level, language);

                        List<Word> wordsToSave = wordsFromApi.stream()
                                .map(wordString -> Word.builder()
                                        .id(UUID.randomUUID().toString())
                                        .word(wordString)
                                        .shuffledWord(wordService.shuffleWord(wordString)) // Kelimeyi karıştır
                                        .build())
                                .collect(Collectors.toList());
                        ScrambledWord scrambledWord = ScrambledWord.builder()
                                .words(wordsToSave)
                                .level(level)
                                .language(language)
                                .createdAt(LocalDateTime.now())
                                .build();
                        if (wordsFromApi != null && wordsFromApi.size() > 0) {
                            wordRepository.save(scrambledWord);

                            log.info("{} words for level {} {} successfully saved with language {}.",
                                    wordsToSave.size(), level.name(), level.getKey(), language.name());
                            success = true;
                        }
                        Thread.sleep(15000);
                    } catch (JsonMappingException e) {
                        retryCount++;
                        log.error("JSON mapping error. This is often a temporary API issue. Retrying... (Attempt {} of 3)", retryCount, e);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        retryCount++;
                        log.error("An unexpected error occurred during word ingestion. Retrying... (Attempt {} of 3)", retryCount, e);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        log.info("Word ingestion cron job completed. Current Time: {}", new Date());
    }


}