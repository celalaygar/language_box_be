package com.game.find.word.ScrambledWord.controller;

import com.game.find.word.ScrambledWord.dto.ScrambledWordResponseDto;
import com.game.find.word.ScrambledWord.entity.Words;
import com.game.find.word.ScrambledWord.service.WordsService;
import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.ScrambledWord.entity.ScrambledWord;
import com.game.find.word.ScrambledWord.service.ScrambledWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(ApiPaths.ScrambledWordCtrl.CTRL)
@RequiredArgsConstructor
@Tag(name = "Word public Controller", description = "API endpoints for the Word Finding Game")
public class ScrambledWordController {

    private final ScrambledWordService scrambledWordService;
    private final WordsService service;

    @GetMapping("/levels")
    @Operation(
            summary = "Get all English levels",
            description = "Returns all English proficiency levels (A1, A2, B1, B2, C1, C2)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public List<EnglishLevel> getAllLevels() {
        return Arrays.asList(EnglishLevel.values());
    }

    @PostMapping("/findAll")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public Set<String> findAll(@RequestBody WordPageRequestDto request) {
        return service.findAll(request.getLanguage(), request.getLevel());
    }



    @PostMapping("/getAllBySequenceNumber")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public List<Words> getAllBySequenceNumber(@RequestBody WordPageRequestDto request) {
        return service.getAllBySequenceNumber(request.getSequenceNumber(),
                request.getLanguage(), request.getLevel());
    }
}
