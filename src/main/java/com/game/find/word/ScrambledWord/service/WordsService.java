package com.game.find.word.ScrambledWord.service;


import com.game.find.word.ScrambledWord.dto.ScrambledWordResponseDto;
import com.game.find.word.ScrambledWord.entity.*;
import com.game.find.word.ScrambledWord.model.Word;
import com.game.find.word.ScrambledWord.repository.ScrambledWordRepository;
import com.game.find.word.ScrambledWord.repository.WordsRepository;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordsService {

    @Value("${app.words.default-count:20}")
    private int defaultCount;
    private final ScrambledWordRepository scrambledWordRepository;
    private final WordsRepository repository;
    private final MongoTemplate mongoTemplate;

    public String saveWords() {
        for (Language language : Language.values()) {
            for (EnglishLevel level : EnglishLevel.values()) {

                // 👇 ESKİSİ: List<ScrambledWord> list = scrambledWordRepository.findByLanguageAndLevelAndIsRead(language, level, false);
                // 👇 YENİSİ: Yeni metot, 'read' alanı false veya null olan ve 'words' listesi dolu olanları getirir.
                List<ScrambledWord> list = scrambledWordRepository
                        .findUnreadAndNonEmptyWordsByLanguageAndLevel(language, level);

                List<Word> wordsList = list.stream()
                        .flatMap(scrambled -> scrambled.getWords().stream())
                        .map(word -> {
                            // Word içindeki Hint kontrolü doğru, sadece null ise (değere sahip değilse) boş değer atar.
                            if (ObjectUtils.isEmpty(word.getHint())) {
                                word.setHint("____");
                            }
                            return word;
                        })
                        .toList();


                Set<String> setList = wordsList.stream()
                        .map(item -> item.getWord()).collect(Collectors.toSet());

                // ... mevcut kelimeleri kontrol edip yeni kelimeleri oluşturan ve kaydeden kısım ...
                List<Words> wordslist = setList.stream()
                        .map(item -> {
                            if (repository.findByWordAndLanguageAndLevel(item, language, level).isEmpty()) {
                                return Words.builder()
                                        .level(level)
                                        .language(language)
                                        .word(item)
                                        .shuffledWord(shuffleWord(item))
                                        .createdAt(LocalDateTime.now())
                                        .hint("____")
                                        .build();
                            }
                            return null; // zaten varsa null döndür
                        })
                        .filter(Objects::nonNull) // null olanları filtrele
                        .collect(Collectors.toList());

                if (!wordslist.isEmpty()) {
                    repository.saveAll(wordslist);
                }

                if (!list.isEmpty()) {
                    list.forEach(scrambledWord -> scrambledWord.setRead(true));

                    scrambledWordRepository.saveAll(list);
                }
            }
        }
        return "ok";
    }

    public ScrambledWordResponseDto getRandomScrambledWords(Language language, EnglishLevel level, Integer count) {
        if (count == null || count <= 0) {
            count = defaultCount;
        }
        MatchOperation matchStage = match(
                org.springframework.data.mongodb.core.query.Criteria
                        .where("language").is(language)
                        .and("level").is(level)
        );

        // 2. Sample -> rastgele count kadar veri çek
        SampleOperation sampleStage = sample(count);

        // 3. Aggregation -> pipeline birleştir
        Aggregation aggregation = Aggregation.newAggregation(matchStage, sampleStage);
        List<Words> list = mongoTemplate.aggregate(aggregation, "words", Words.class).getMappedResults();
        ScrambledWordResponseDto response = new ScrambledWordResponseDto();

        if (!CollectionUtils.isEmpty(list)) {
            response.setLevel(level);
            response.setId(list.get(0).getId());
            response.setLanguage(language);
            response.setWords(list);
            response.setCreatedAt(list.get(0).getCreatedAt());
            response.setCount(list.size());
        }
        return response;
    }

    public String shuffleWord(String word) {
        List<Character> characters = word.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        characters.forEach(sb::append);
        return sb.toString();
    }

    public Set<String> findAll(Language language, EnglishLevel level) {

        List<Words> list = repository.findByLanguageAndLevel(language, level);
        Set<String> response = list.stream().map(item -> item.getWord()).collect(Collectors.toSet());
        return response;
    }

    public List<Words> getAll() {

        List<Words> list = repository.findAll();
        return list;
    }
}
