package com.game.find.word.VoiceMatch.controller;

import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.VoiceMatch.service.VoiceMatchService;
import com.game.find.word.base.util.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.VoiceMatchCtrl.CTRL)
@RequiredArgsConstructor
@Tag(name = "Word public Controller", description = "API endpoints for the Word Finding Game")
public class VoiceMatchController {

    private final VoiceMatchService wordService;


    @PostMapping("/today")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public List<VoiceMatch> getTodayWords(@RequestBody WordPageRequestDto request) {
        return wordService.getRandom(request.getLanguage(), request.getLevel(), request.getCount());
    }
    @PostMapping("/getAllBySequenceNumber")
    @Operation(
            summary = "Get all words by level",
            description = "Returns all words for the given English level as a single page response",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval")
            }
    )
    public List<VoiceMatch> getAllBySequenceNumber(@RequestBody WordPageRequestDto request) {
        return wordService.getAllBySequenceNumber(request.getSequenceNumber(),
                request.getLanguage(), request.getLevel());
    }
}
