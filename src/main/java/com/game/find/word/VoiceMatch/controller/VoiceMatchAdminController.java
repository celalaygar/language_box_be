package com.game.find.word.VoiceMatch.controller;


import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.VoiceMatch.service.VoiceMatchService;
import com.game.find.word.base.util.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.VoiceMatchAdminCtrl.CTRL)
@RequiredArgsConstructor
public class VoiceMatchAdminController {

    private final VoiceMatchService service;

    @PostMapping("/bulkSaveData")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public Integer bulkSaveWords(@RequestBody List<VoiceMatch> list) {
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
