package com.game.find.word.base.controller;

import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.ScrambledWord.service.ScrambledWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.GlobalCtrl.CTRL)
@RequiredArgsConstructor
@Tag(name = "Word public Controller", description = "API endpoints for the Word Finding Game")
public class GlobalController {

    private final ScrambledWordService wordService;


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

    @GetMapping("/languages")
    @Operation(
            summary = "Get all English levels",
            description = "Returns all English proficiency levels (A1, A2, B1, B2, C1, C2)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public List<Language> getAllLanguages() {
        return Arrays.asList(Language.values());
    }



}
