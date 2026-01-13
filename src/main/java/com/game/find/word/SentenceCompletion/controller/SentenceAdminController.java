package com.game.find.word.SentenceCompletion.controller;


import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.SentenceCompletion.service.SentenceCompletionService;
import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SentenceAdminCtrl.CTRL)
@RequiredArgsConstructor
public class SentenceAdminController {

    private final SentenceCompletionService service;


    @PostMapping("/bulkSaveData")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public Integer bulkSaveWords(@RequestBody List<SentenceCompletion> list) {
        return service.bulkSaveData(list);
    }

    @GetMapping("/reindexAllData")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public Boolean reindexAllWords() {
        return service.reindexAllData();
    }
}
