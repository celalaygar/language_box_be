package com.game.find.word.GridChallenge.service;


import com.game.find.word.GridChallenge.entity.GridChallenge;
import com.game.find.word.GridChallenge.dto.GridChallengeCreateRequest;
import com.game.find.word.GridChallenge.repository.GridChallengeRepository;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class GridChallengeService {

    @Value("${app.grid-challange.default-count:1}")
    private int defaultCount;

    private final MongoTemplate mongoTemplate;

    private final GridChallengeRepository repository;


    @Transactional
    public List<GridChallenge> saveAllChallenges(List<GridChallengeCreateRequest> requests) {
        List<GridChallenge> challenges = requests.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());


        return repository.saveAll(challenges);
    }

    public List<GridChallenge> findChallengesByLevelAndLanguage(EnglishLevel level, Language language) {
        return repository.findAllByLevelAndLanguage(level, language);
    }

    public List<GridChallenge> getRandom( EnglishLevel level,Language language, Integer count) {
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
        List<GridChallenge> list = mongoTemplate.aggregate(
                aggregation,
                "gridChallenges", // collection adı
                GridChallenge.class
        ).getMappedResults();

        return list;
    }

    public void deleteChallengeById(Long id) {
        repository.deleteById(id);
    }

    private GridChallenge convertToEntity(GridChallengeCreateRequest dto) {
        GridChallenge entity = new GridChallenge();
        // entity.setId(...); // ID yönetimi burada yapılabilir
        entity.setSettings(dto.getSettings());
        entity.setGrid(dto.getGrid());
        entity.setWords(dto.getWords());
        entity.setWordList(dto.getWordList());
        entity.setLevel(dto.getLevel());
        entity.setLanguage(dto.getLanguage());
        entity.setCreatedAt(LocalDateTime.now()); // Oluşturulma zamanı otomatik ayarlanır
        return entity;
    }
}