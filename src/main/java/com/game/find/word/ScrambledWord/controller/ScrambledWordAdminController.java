package com.game.find.word.ScrambledWord.controller;


import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.ScrambledWord.service.WordsService;
import com.game.find.word.base.util.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ScrambledWordAdminCtrl.CTRL)
@RequiredArgsConstructor
public class ScrambledWordAdminController {

    private final WordsService service;

    @PostMapping("/bulkSaveData")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public Integer bulkSaveWords(@RequestBody List<Words> list) {
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
