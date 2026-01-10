package com.game.find.word.version.repository;

import com.game.find.word.version.entity.AppVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppVersionRepository extends MongoRepository<AppVersion, String> {
}