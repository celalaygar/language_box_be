package com.game.find.word.VoiceMatch.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.VoiceMatch.dto.VoiceMatchDto;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.VoiceMatch.repository.VoiceMatchRepository;
import com.game.find.word.googleAI.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoiceMatchScheduleService {

    private final VoiceMatchRepository repository;
    private final VoiceMatchService service;
    private final GeminiService geminiService;

    /**
     * Fetches English words from DeepSeek API for various levels and languages
     * and saves them to the database.
     * This cron job runs daily at 00:05.
     */

    //@Scheduled(cron = "0 30 * * * *")
    public void fetchAndSaveEntity() {
        log.info("VoiceMatchScheduleService.fetchAndSaveEntity ingestion cron job started. Current Time: {}", new Date());
        for (Language language : Language.values()) {
            for (EnglishLevel level : EnglishLevel.values()) {
//                List<VoiceMatch> todayList = service.getToday(  language ,level);
//                if (!(CollectionUtils.isEmpty(todayList))) {
//                    log.info("VoiceMatchScheduleService.todayList list for level {} and language {} already exist. Skipping.", level.getKey(), language.name());
//                    continue;
//                }

                int retryCount = 0;
                boolean success = false;
                while (retryCount < 3 && !success) {
                    try {
                        log.info("Attempt #{} - Fetching words for level {} {} with language {}.",
                                retryCount + 1, level.name(), level.getKey(), language.name());

                        List<VoiceMatchDto> dtos = geminiService.getVoiceMatchItems( level, language);
                        if (CollectionUtils.isEmpty(dtos)) {
                            System.out.println("VoiceMatchScheduleService.fetchAndSaveEntity for level " + level.name() + " successfully saved with language " + language.name());
                            continue;
                        }
                        List<VoiceMatch> list = dtos.stream().map(
                                item -> VoiceMatch.builder()
                                        .correctSentence(item.getCorrectSentence())
                                        .similarOptions(item.getSimilarOptions())
                                        .focusWords(item.getFocusWords())
                                        .language(language)
                                        .level(level)
                                        .createdAt(LocalDateTime.now())
                                        .build()
                        ).collect(Collectors.toList());
                        repository.saveAll(list);

                        log.info("VoiceMatchScheduleService.fetchAndSaveEntity WordGuessingGame for level {} {} successfully saved with language {}.",
                                level.name(), level.getKey(), language.name());
                        success = true;
                        Thread.sleep(15000);
                    } catch (JsonMappingException e) {
                        retryCount++;
                        log.error("VoiceMatchScheduleService.fetchAndSaveEntity JSON mapping error. This is often a temporary API issue. Retrying... (Attempt {} of 3)", retryCount, e);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        retryCount++;
                        log.error("VoiceMatchScheduleService.fetchAndSaveEntity An unexpected error occurred during word ingestion. Retrying... (Attempt {} of 3)", retryCount, e);
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        log.info("VoiceMatchScheduleService.fetchAndSaveEntity ingestion cron job completed. Current Time: {}", new Date());
    }


}
