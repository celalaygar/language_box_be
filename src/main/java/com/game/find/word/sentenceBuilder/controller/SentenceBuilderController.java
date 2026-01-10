package com.game.find.word.sentenceBuilder.controller;


import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.sentenceBuilder.service.SentenceBuilderService;
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
}