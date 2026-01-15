package com.game.find.word.ScrambledWord.service;


import com.game.find.word.base.model.EnglishLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {


    @Override
    public void run(String... args) {
        System.out.println("⚡ Word collection initialization started...  " + new Date());

        // A1 Seviyesi
        List<String> a1Words = List.of("apple", "book", "cat", "dog", "school", "call", "shout", "what", "where",
                "water", "table", "chair", "house", "pen", "orange", "help", "milk", "water", "car", "bus", "want",
                "eat", "drink", "walk", "speak", "listen", "run", "cry", "waiting", "stand", "yellow", "blue", "make",
                "drive", "spoon", "box", "player", "ball", "football", "word", "sentence", "look", "bathroom", "live",
                "white", "black", "fast", "speed", "start", "mouse", "cat", "dog", "woman", "human", "animal");

        // A2 Seviyesi
        List<String> a2Words = List.of("family", "travel", "market", "teacher", "friend", "worker", "tie",
                "movie", "music", "language", "english", "germany", "spain", "france", "england", "holiday", "scream",
                "morning", "afternoon", "evening", "ambulance", "actor", "act", "button", "pencil", "create", "bellow",
                "bulk", "standing", "noise", "spider", "leon", "captain", "think", "about", "divide", "throw", "claim",
                "soccer", "quest", "guard", "search", "elephant", "bat", "monkey", "tiger", "weather", "wind", "rain",
                "join", "right", "left", "back", "own", "update", "meat", "strawberry", "mirror", "trip", "journey");

        // B1 Seviyesi
        List<String> b1Words = List.of("computer", "library", "history", "weather", "science", "unity",
                "village", "garden", "student", "kitchen", "hospital", "notebook", "computer", "laptop", "screech",
                "machine", "artificial", "ability", "account", "advice", "apartment", "arrange", "arrival", "opposite",
                "article", "audience", "balance", "benefit", "challenge", "condition", "consider", "contain", "permission",
                "service", "listing", "controller", "pending", "capture", "butcher", "sticker", "attention","leave",
                "coding", "type", "discuss", "enjoy", "understand", "feel", "sense", "notice", "share", "combination",
                "contrast", "contribute", "decision", "demand", "describe", "development");

        // B2 Seviyesi
        List<String> b2Words = List.of("university", "development", "engineer", "mathematics", "government",
                "company", "business", "strategy", "politics", "society", "abstract", "approach", "assignment", "authorization",
                "lecture", "conference", "interview", "appreciate", "perceive", "interest", "participation", "allowance",
                "assume", "aware", "collapse", "commitment", "component", "consequence", "consistent", "construct",
                "contribute", "convention", "criteria", "dedicate", "define", "dimension", "emphasis", "enhance", "establish");

        // C1 Seviyesi
        List<String> c1Words = List.of("philosophy", "literature", "architecture", "psychology", "economics",
                "analysis", "criticism", "revolution", "influence", "perspective", "acknowledge", "advocate", "coherent", "comprehensive", "contemplate",
                "conventional", "correspond", "critique", "deduction", "elaborate",
                "endorse", "envision", "equivalent", "formulate", "illustrate",
                "implement", "integrate", "interpret", "manipulate", "prioritize");

        // C2 Seviyesi
        List<String> c2Words = List.of("consciousness", "metaphorically", "juxtaposition", "unprecedented", "interdisciplinary",
                "phenomenology", "transcendental", "responsibility", "comprehensive", "representation", "acquiesce", "ameliorate", "anachronism", "circumvent", "coalesce",
                "conflagration", "consummate", "deleterious", "disseminate", "ebullient",
                "egregious", "enervate", "exacerbate", "expedite", "extemporaneous",
                "incontrovertible", "obfuscate", "perspicacious", "propinquity", "recapitulate");

//        saveWords(a1Words, EnglishLevel.A1);
//        saveWords(a2Words, EnglishLevel.A2);
//        saveWords(b1Words, EnglishLevel.B1);
//        saveWords(b2Words, EnglishLevel.B2);
//        saveWords(c1Words, EnglishLevel.C1);
//        saveWords(c2Words, EnglishLevel.C2);

        System.out.println("✅ Word collection initialization finished!  " + new Date());
    }

    private void saveWords(List<String> words, EnglishLevel level) {
//        words.forEach(word -> {
//            if (!wordRepository.existsByWord(word)) {
//                Word newWord = Word.builder()
//                        .word(word)
//                        .shuffledWord(wordService.shuffleWord(word))
//                        .level(level)
//                        .createdAt(LocalDateTime.now())
//                        .build();
//                wordRepository.save(newWord);
//                System.out.println("Inserted word: " + word + " (" + level + ")");
//            } else {
//                System.out.println("Skipped existing word: " + word);
//            }
//        });
    }
}
