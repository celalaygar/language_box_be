package com.game.find.word.SentenceCompletion.controller;


import com.game.find.word.base.model.BaseGameResponse;
import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.SentenceCompletion.service.SentenceCompletionService;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SentenceCtrl.CTRL)
@RequiredArgsConstructor
public class SentenceController {

    private final SentenceCompletionService sentenceService;
    @PostMapping("/today")
    public List<SentenceCompletion> getSentences(@RequestBody WordPageRequestDto request) {
        return sentenceService.getRandomSentences(request.getLanguage(), request.getLevel(), request.getCount());
    }

    @GetMapping("/findAll")
    public List<SentenceCompletion> findAll() {
        return sentenceService.findAll();
    }

    @PostMapping("/getAllBySequenceNumber")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public BaseGameResponse<SentenceCompletion> getAllBySequenceNumber(@RequestBody WordPageRequestDto request) {
        return sentenceService.getAllBySequenceNumber(request.getSequenceNumber(),
                request.getLanguage(), request.getLevel());
    }
}