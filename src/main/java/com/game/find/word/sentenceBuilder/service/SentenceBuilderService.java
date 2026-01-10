package com.game.find.word.sentenceBuilder.service;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.game.find.word.SentenceCompletion.repository.SentenceCompletionRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.googleAI.service.GeminiService;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGameDto;
import com.game.find.word.sentenceBuilder.repository.SentenceBuildGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;


import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SentenceBuilderService {
    @Value("${app.sentence.default-count:3}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;

    private final GeminiService geminiService;
    private final SentenceCompletionRepository sentenceCompletionRepository;
    private final SentenceBuildGameRepository sentenceBuildGameRepository;


    public List<SentenceBuildGame> getTodaySentencesForBuildGame(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<SentenceBuildGame> list = sentenceBuildGameRepository.findByLanguageAndLevelAndCreatedAtBetween(
                language, level, startOfDay, endOfDay);

        log.info("SentenceBuilderService.getTodaySentencesForBuildGame " + new Date());
        return list;
    }


    public List<SentenceBuildGameDto> getSentencesForBuildGame(EnglishLevel level, Language language) throws JsonMappingException {
        List<SentenceBuildGameDto> sentences = geminiService.getSentencesForBuildGame(level, language);
        return sentences;
    }

    public List<SentenceBuildGame> getRandomSentences(Language language, EnglishLevel level, Integer count) {
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

        return mongoTemplate.aggregate(
                aggregation,
                "sentence_build", // collection adı
                SentenceBuildGame.class
        ).getMappedResults();
    }


    public List<SentenceBuildGame> findAll() {
        return sentenceBuildGameRepository.findAll();
    }

    public SentenceBuildGame save(SentenceBuildGame game) {
        game.setCreatedAt(LocalDateTime.now());
        return sentenceBuildGameRepository.save(game);
    }

    public List<SentenceBuildGame> saveAll(List<SentenceBuildGame> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return sentenceBuildGameRepository.saveAll(list);
    }
}