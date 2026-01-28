package com.game.find.word.ListenWord.controller;


import com.game.find.word.ListenWord.entity.ListenWord;
import com.game.find.word.ListenWord.service.ListenWordService;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.base.model.BaseGameResponse;
import com.game.find.word.base.util.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ListenWordCtrl.CTRL)
@RequiredArgsConstructor
public class ListenWordController {

    private final ListenWordService sentenceService;
    @PostMapping("/today")
    public List<ListenWord> getSentences(@RequestBody WordPageRequestDto request) {
        return sentenceService.getRandomSentences(request.getLanguage(), request.getLevel(), request.getCount());
    }

    @GetMapping("/findAll")
    public List<ListenWord> findAll() {
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
    public BaseGameResponse<ListenWord> getAllBySequenceNumber(@RequestBody WordPageRequestDto request) {
        return sentenceService.getAllBySequenceNumber(request.getSequenceNumber(),
                request.getLanguage(), request.getLevel());
    }
}