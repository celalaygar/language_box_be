package com.game.find.word.ListenWord.controller;


import com.game.find.word.ListenWord.entity.ListenWord;
import com.game.find.word.ListenWord.service.ListenWordService;
import com.game.find.word.base.util.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ListenWordAdminCtrl.CTRL)
@RequiredArgsConstructor
public class ListenWordAdminController {

    private final ListenWordService service;


    @PostMapping("/bulkSaveData")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public Integer bulkSaveWords(@RequestBody List<ListenWord> list) {
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
