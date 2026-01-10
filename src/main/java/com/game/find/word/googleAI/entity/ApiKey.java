package com.game.find.word.googleAI.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "api_keys")
public class ApiKey {

    @Id
    private String id;
    private String key;
    private boolean isActive;
    private ApiKeyType apiKeyType;

}