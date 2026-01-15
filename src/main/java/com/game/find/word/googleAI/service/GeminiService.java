package com.game.find.word.googleAI.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.MatchSentence.dto.MatchSentenceDto;
import com.game.find.word.googleAI.entity.ApiKey;
import com.game.find.word.googleAI.dto.*;
import com.game.find.word.googleAI.entity.ApiKeyType;
import com.game.find.word.googleAI.repository.ApiKeyRepository;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGameDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ApiKeyRepository apiKeyRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Veritabanından yüklenecek olan API anahtarları listesi
    private List<String> apiKeys = new ArrayList<>();
    // Güvenli sayaç kullanımı için AtomicInteger
    private final AtomicInteger currentApiKeyIndex = new AtomicInteger(0);

    // Uygulama başladığında anahtarları veritabanından yükle
    @PostConstruct
    public void loadApiKeys() {
        this.apiKeys = apiKeyRepository.findByIsActiveAndApiKeyType(true, ApiKeyType.GEMINI)
                .stream()
                .map(ApiKey::getKey)
                .collect(Collectors.toList());
        if (this.apiKeys.isEmpty()) {
            throw new IllegalStateException("No active API keys found in the database.");
        }
    }

    private String getCurrentApiKey() {
        return apiKeys.get(currentApiKeyIndex.get());
    }

    // API anahtarını değiştiren ve bekleme süresi ekleyen yardımcı metot
    private void switchApiKey() throws InterruptedException {
        int newIndex = (currentApiKeyIndex.get() + 1) % apiKeys.size();
        currentApiKeyIndex.set(newIndex);
        System.out.println("Current key index: " + newIndex);
        System.out.println("Switching to a new API key " + apiKeys.get(currentApiKeyIndex.get()) + ".");
        Thread.sleep(8000); // Her token değişiminde 8 saniye bekle
    }

    public List<SentenceCompletion> getSentenceCompletions(EnglishLevel level, Language language) throws JsonMappingException {
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + getCurrentApiKey();

                PromptPart promptPart = new PromptPart("Provide an array of 20 JSON objects for a sentence completion game. " +
                        "Each object should have two keys: 'sentence' (the sentence to be completed) and 'answer' " +
                        "(the correct word). The sentences should be in " + language.getDescription() + ", at an " + level + " (" + level.getKey() + ") level, " +
                        "and should contain a blank marked by '____'. The answers must be only **nouns** or **adverbs**," +
                        " with a balanced distribution of both. Only provide the JSON array, nothing else.");
                Content content = new Content(Collections.singletonList(promptPart));
                RequestPayload requestPayload = new RequestPayload(Collections.singletonList(content));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<RequestPayload> entity = new HttpEntity<>(requestPayload, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String responseBody = response.getBody();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                    if (textNode.isMissingNode() || !textNode.isTextual()) {
                        throw new RuntimeException("API response text is missing or not valid.");
                    }
                    String contentJson = textNode.asText().replace("```json\n", "").replace("\n```", "").trim();
                    return objectMapper.readValue(contentJson, new TypeReference<List<SentenceCompletion>>() {
                    });
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.err.println("Too Many Requests error GeminiService.getSentenceCompletions. Changing API key and retrying... (Attempt " + (retryCount + 1) + ")");
                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (JsonMappingException e) {
                System.err.println("JSON mapping error GeminiService.getSentenceCompletions. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception error GeminiService.getSentenceCompletions. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");
                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            retryCount++;
        }
        return Collections.emptyList();
    }


    public List<SentenceBuildGameDto> getSentencesForBuildGame(EnglishLevel level, Language language) throws JsonMappingException {
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + getCurrentApiKey();

                PromptPart promptPart = new PromptPart(
                        "Provide an array of exactly 20 JSON objects for a sentence building game. Ensure the JSON array is perfectly formed with no extra characters before or after it, and is parseable. Each object must have two keys: " +
                                "'sentence' (the complete sentence) and 'mixedWords' (an array of strings containing the words from the sentence " +
                                "in a shuffled order. The final word in the sentence, along with its end punctuation (e.g., '.', '?', '!'), " +
                                "should be treated as a single string and placed within the mixedWords array). The sentences should be in " + language.getDescription() + ", " +
                                "at an " + level + " (" + level.getKey() + ") level, and related to common daily life topics. " +
                                "Only provide the JSON array, nothing else."
                );
                Content content = new Content(Collections.singletonList(promptPart));
                RequestPayload requestPayload = new RequestPayload(Collections.singletonList(content));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<RequestPayload> entity = new HttpEntity<>(requestPayload, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String responseBody = response.getBody();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                    if (textNode.isMissingNode() || !textNode.isTextual()) {
                        throw new RuntimeException("API response text is missing or not valid.");
                    }
                    String contentJson = textNode.asText().replace("```json\n", "").replace("\n```", "").trim();
                    return objectMapper.readValue(contentJson, new TypeReference<List<SentenceBuildGameDto>>() {
                    });
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.err.println("Too Many Requests error GeminiService.getSentencesForBuildGame. Changing API key and retrying... (Attempt " + (retryCount + 1) + ")");
                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (JsonMappingException e) {
                System.err.println("JSON mapping error GeminiService.getSentencesForBuildGame. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception error GeminiService.getSentencesForBuildGame. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");

                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            retryCount++;
        }
        return Collections.emptyList();
    }


    /**
     * Fetches a list of 30 words from the Gemini AI API with a specified mix of nouns, adjectives, and adverbs.
     * The method handles API key rotation and retries for network and JSON mapping issues.
     *
     * @param level    The English level for which to fetch words.
     * @param language The language of the words.
     * @return A list of words as strings.
     * @throws JsonMappingException If the JSON response from the API is malformed.
     */
    public List<String> getEnglishWords(EnglishLevel level, Language language) throws JsonMappingException {
        int retryCount = 0;
        while (retryCount < 5) { // Maksimum 5 deneme
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + getCurrentApiKey();

                // Prompt'u dinamik olarak oluştur
                PromptPart promptPart = new PromptPart(
                        "Provide a JSON array of exactly 60 words in " + language.getDescription() + ". " +
                                "The words should be at a " + level.getKey() + " level. The array must contain " +
                                "25 nouns, 25 adjectives, and 10 adverbs. Only provide the JSON array, nothing else."
                );

                Content content = new Content(Collections.singletonList(promptPart));
                RequestPayload requestPayload = new RequestPayload(Collections.singletonList(content));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<RequestPayload> entity = new HttpEntity<>(requestPayload, headers);

                // API'ye POST isteği gönderme
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String responseBody = response.getBody();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                    if (textNode.isMissingNode() || !textNode.isTextual()) {
                        throw new RuntimeException("API response text is missing or not valid.");
                    }
                    // Gemini'den gelen JSON'da olabilecek ```json\n ve \n``` temizliği
                    String contentJson = textNode.asText().replace("```json\n", "").replace("\n```", "").trim();

                    // Gelen JSON'u List<String>'e çevirme
                    return objectMapper.readValue(contentJson, new TypeReference<List<String>>() {
                    });
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.err.println("GeminiService.getEnglishWords  Too Many Requests error GeminiService.getMixedWords. Changing API key and retrying... (Attempt " + (retryCount + 1) + ")");
                try {
                    switchApiKey();
                    Thread.sleep(8000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (JsonMappingException e) {
                System.err.println("GeminiService.getEnglishWords  JSON mapping error GeminiService.getMixedWords. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("GeminiService.getEnglishWords Exception error GeminiService.getMixedWords. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");

                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            retryCount++;
        }
        return Collections.emptyList();
    }

    /**
     * Fetches a list of 5 listening discrimination game items for a given level and language.
     * The game focuses on homophones or similar-sounding words in sentences.
     *
     * @param level    The English level (e.g., A2).
     * @param language The language of the sentences.
     * @return A List of DTOs, each representing a single game item.
     * @throws JsonMappingException If the JSON response from the API is malformed.
     */
    public List<MatchSentenceDto> getVoiceMatchItems(EnglishLevel level, Language language) throws JsonMappingException {
        int retryCount = 0;

        // Sabit öğe sayısı: 5
        final int ITEM_COUNT = 5;

        while (retryCount < 5) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + getCurrentApiKey();

                // SADECE ITEMS ARRAY'İNİ ÜRETEN PROMPT
                // %1$s: languageDescription, %2$s: levelKey, %3$d: ITEM_COUNT
                String promptTemplate = "Generate a single JSON array containing exactly %3$d objects for a Listening Discrimination game. " +
                        "The content must be in %1$s at the %2$s level. " +
                        "Each object MUST NOT include an 'id' field. " + // <-- ID KURALI VURGULANDI
                        "Each object must have a 'correct_sentence' and an array of 'similar_options' (containing the correct sentence and two phonetically very close, but incorrect, sentences by using homophones or near-minimal pairs). " +
                        "Include the 'focus_words' array listing the similar-sounding words used. " +
                        "Only output the JSON array, nothing else. **Ensure there are exactly 5 objects.**";


                String prompt = String.format(promptTemplate,
                        language.getDescription(),
                        level.getKey(),
                        ITEM_COUNT);

                PromptPart promptPart = new PromptPart(prompt);
                Content content = new Content(Collections.singletonList(promptPart));
                RequestPayload requestPayload = new RequestPayload(Collections.singletonList(content));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<RequestPayload> entity = new HttpEntity<>(requestPayload, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);


                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    String responseBody = response.getBody();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode textNode = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
                    if (textNode.isMissingNode() || !textNode.isTextual()) {
                        throw new RuntimeException("API response text is missing or not valid.");
                    }
                    String contentJson = textNode.asText().replace("```json\n", "").replace("\n```", "").trim();

                    // DTO listesine çevirme
                    return objectMapper.readValue(contentJson, new TypeReference<List<MatchSentenceDto>>() {});

                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.err.println("Too Many Requests error GeminiService.getListeningDiscriminationItems. Changing API key and retrying... (Attempt " + (retryCount + 1) + ")");
                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (JsonMappingException e) {
                System.err.println("JSON mapping error GeminiService.getListeningDiscriminationItems. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception error GeminiService.getListeningDiscriminationItems. Retrying... Attempt " + (retryCount + 1) + " for " + language.name() + " and " + level.name() + ".");
                try {
                    switchApiKey();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            retryCount++;
        }
        return Collections.emptyList();
    }
}