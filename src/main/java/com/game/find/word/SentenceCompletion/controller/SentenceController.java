package com.game.find.word.SentenceCompletion.controller;


import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.SentenceCompletion.service.SentenceCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SentenceCtrl.CTRL)
@RequiredArgsConstructor
public class SentenceController {

    private final SentenceCompletionService sentenceService; // GeminiService yerine SentenceService enjekte edildi

    @PostMapping("/today")
    public List<SentenceCompletion> getSentences(@RequestBody WordPageRequestDto request) {
        return sentenceService.getRandomSentences(request.getLanguage(), request.getLevel(), request.getCount());
    }
}