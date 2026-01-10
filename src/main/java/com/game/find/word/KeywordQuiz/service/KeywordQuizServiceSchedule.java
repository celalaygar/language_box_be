package com.game.find.word.KeywordQuiz.service;


import com.game.find.word.KeywordQuiz.repository.KeywordQuizRepository;
import com.game.find.word.googleAI.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordQuizServiceSchedule {

    private final KeywordQuizRepository repository;
    private final KeywordQuizService service;
    private final GeminiService geminiService;

    /**
     * Fetches English words from DeepSeek API for various levels and languages
     * and saves them to the database.
     * This cron job runs daily at 00:05.
     */
    /*
    @Scheduled(cron = "0 51 20 * * *")
    public void fetchAndSaveKeywordQuizs() {
        log.info("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs ingestion cron job started. Current Time: {}", new Date());

        for (Language language : Language.values()) {
            for (EnglishLevel level : EnglishLevel.values()) {
                List<KeywordQuiz> existingWords = service.getTodayWords(language, level);
                if (!ObjectUtils.isEmpty(existingWords)) {
                    log.info("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs list for level {} and language {} already exist. Skipping.", level.getKey(), language.name());
                    continue;
                }

                int retryCount = 0;
                boolean success = false;
                while (retryCount < 3 && !success) {
                    try {
                        log.info("Attempt #{} - Fetching words for level {} {} with language {}.",
                                retryCount + 1, level.name(), level.getKey(), language.name());

                        List<KeywordQuizDto> list = geminiService.getKeywordQuizsContent(level, language);
                        if (CollectionUtils.isEmpty(list)) {
                            System.out.println("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs for level " + level.name() + " successfully saved with language " + language.name());
                            continue;
                        }

                        List<KeywordQuiz> wordsToSave = list.stream()
                                .map(data -> KeywordQuiz.builder()
                                        .text(data.getText())
                                        .correctKeyword(data.getCorrectKeyword())
                                        .keywords(data.getKeywords())
                                        .language(language)
                                        .level(level)
                                        .createdAt(LocalDateTime.now())
                                        .build())
                                .collect(Collectors.toList());

                        if (!CollectionUtils.isEmpty(list)) {
                            repository.saveAll(wordsToSave);

                            log.info("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs {} WordGuessingGame for level {} {} successfully saved with language {}.",
                                    wordsToSave.size(), level.name(), level.getKey(), language.name());
                            success = true;
                        }
                        Thread.sleep(15000);
                    } catch (JsonMappingException e) {
                        retryCount++;
                        log.error("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs JSON mapping error. This is often a temporary API issue. Retrying... (Attempt {} of 3)", retryCount, e);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        retryCount++;
                        log.error("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs An unexpected error occurred during word ingestion. Retrying... (Attempt {} of 3)", retryCount, e);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        log.info("KeywordQuizServiceSchedule.fetchAndSaveKeywordQuizs ingestion cron job completed. Current Time: {}", new Date());
    }

     */
}