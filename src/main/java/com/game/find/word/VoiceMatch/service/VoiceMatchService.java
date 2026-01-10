package com.game.find.word.VoiceMatch.service;


import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.SentenceCompletion.repository.SentenceCompletionRepository;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.VoiceMatch.repository.VoiceMatchRepository;
import com.game.find.word.googleAI.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceMatchService {
    @Value("${app.sentence.default-count:3}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;

    private final GeminiService geminiService;
    private final SentenceCompletionRepository sentenceCompletionRepository;
    private final VoiceMatchRepository repository;


    public List<VoiceMatch> getToday(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<VoiceMatch> list = repository.findByLanguageAndLevelAndCreatedAtBetween(
                language, level, startOfDay, endOfDay);

        log.info("VoiceMatchService.getTodayWordSearchGame " + new Date());
        return list;
    }


    public List<VoiceMatch> getRandom(Language language, EnglishLevel level, Integer count) {
        if (count == null || count <= 0) {
            count = defaultCount;
        }

        MatchOperation matchStage = match(
                org.springframework.data.mongodb.core.query.Criteria
                        .where("language").is(language)
                        .and("level").is(level)
        );

        SampleOperation sampleStage = sample(count);

        Aggregation aggregation = newAggregation(matchStage, sampleStage);
        List<VoiceMatch> list = mongoTemplate.aggregate(
                aggregation,
                "voice_match", // collection adı
                VoiceMatch.class
        ).getMappedResults();
        // her VoiceMatch içindeki similarOptions listesini rastgele sırala
        for (VoiceMatch vm : list) {
            if (vm.getSimilarOptions() != null && !vm.getSimilarOptions().isEmpty()) {
                Collections.shuffle(vm.getSimilarOptions());
            }
        }
        return list;
    }


    public List<VoiceMatch> findAll() {
        return repository.findAll();
    }

    public VoiceMatch save(VoiceMatch game) {
        game.setCreatedAt(LocalDateTime.now());
        return repository.save(game);
    }

    public List<VoiceMatch> saveAll(List<VoiceMatch> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return repository.saveAll(list);
    }
}