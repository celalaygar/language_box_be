package com.game.find.word.KeywordQuiz.service;


import com.game.find.word.KeywordQuiz.entity.KeywordQuiz;
import com.game.find.word.KeywordQuiz.repository.KeywordQuizRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordQuizService {

    @Value("${app.keyword-quiz.default-count:5}")
    private int defaultCount;
    private final MongoTemplate mongoTemplate;
    private final KeywordQuizRepository repository;


    public List<KeywordQuiz> getTodayWords(Language language, EnglishLevel level) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<KeywordQuiz> list = repository.findByLanguageAndLevelAndCreatedAtBetween(
                language, level, startOfDay, endOfDay);

        log.info("WordService.getTodayWords method called. Fetched {} words for level {} and language {}",
                !CollectionUtils.isEmpty(list) ? list.size() : 0,
                level.getKey(), language.name());

        return list;
    }


    public KeywordQuiz save(KeywordQuiz quiz) {
        quiz.setCreatedAt(LocalDateTime.now());
        return  repository.save(quiz);
    }

    public List<KeywordQuiz> saveAll(List<KeywordQuiz> list) {
        list = list.stream().map(item -> {
            item.setCreatedAt(LocalDateTime.now());
            return item;
        }).collect(Collectors.toList());
        return  repository.saveAll(list);
    }

    public List<KeywordQuiz> findAll() {
        return  repository.findAll();
    }

    public List<KeywordQuiz> getRandomWGGames(Language language, EnglishLevel level, Integer count) {
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
                "keyword_quiz", // collection adı
                KeywordQuiz.class
        ).getMappedResults();
    }
}