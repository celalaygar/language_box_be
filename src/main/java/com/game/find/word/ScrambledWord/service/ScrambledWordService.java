package com.game.find.word.ScrambledWord.service;


import com.game.find.word.ScrambledWord.dto.PageResponseDto;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.ScrambledWord.dto.ScrambledWordDto;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.entity.ScrambledWord;
import com.game.find.word.ScrambledWord.model.Word;
import com.game.find.word.ScrambledWord.repository.ScrambledWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrambledWordService {

    @Value("${app.scrambled-word.default-count:3}")
    private int defaultCount;
    private final ScrambledWordRepository wordRepository;
    private final MongoTemplate mongoTemplate;

    public String shuffleWord(String word) {
        List<Character> characters = word.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        characters.forEach(sb::append);
        return sb.toString();
    }
}
