package com.game.find.word.GridChallenge.repository;


import com.game.find.word.GridChallenge.entity.GridChallenge;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GridChallengeRepository extends MongoRepository<GridChallenge, Long> {

    List<GridChallenge> findAllByLevelAndLanguage(EnglishLevel level, Language language);

    // saveAll ve id bazlı delete metotları MongoRepository'den miras alınmıştır:
    // List<S> saveAll(Iterable<S> entities);
    // void deleteById(ID id);
}