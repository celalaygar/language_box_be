package com.game.find.word.googleAI.repository;

import com.game.find.word.googleAI.entity.ApiKey;
import com.game.find.word.googleAI.entity.ApiKeyType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApiKeyRepository extends MongoRepository<ApiKey, String> {

    List<ApiKey> findByIsActive(boolean isActive);
    List<ApiKey> findByIsActiveAndApiKeyType(boolean isActive, ApiKeyType apiKeyType);

}