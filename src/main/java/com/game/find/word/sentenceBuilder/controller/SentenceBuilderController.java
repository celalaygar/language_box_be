package com.game.find.word.sentenceBuilder.controller;


import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.base.model.BaseGameResponse;
import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.sentenceBuilder.service.SentenceBuilderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SentenceBuilderCtrl.CTRL)
@RequiredArgsConstructor
public class SentenceBuilderController {

    private final SentenceBuilderService sentenceBuilderService;

    /**
     * Retrieves all sentence building game data for a given English level.
     * @param level The English level to filter the sentences by.
     * @return A list of SentenceBuildGame objects.
     */
    @PostMapping("/today")
    public List<SentenceBuildGame> getSentenceBuildGame(@RequestBody WordPageRequestDto request) {
        return sentenceBuilderService.getRandomSentences(request.getLanguage(), request.getLevel(), request.getCount());
    }



    @PostMapping("/getAllBySequenceNumber")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public BaseGameResponse<SentenceBuildGame> getAllBySequenceNumber(@RequestBody WordPageRequestDto request) {
        return sentenceBuilderService.getAllBySequenceNumber(request.getSequenceNumber(),
                request.getLanguage(), request.getLevel());
    }
}