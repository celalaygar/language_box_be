package com.game.find.word.KeywordQuiz.controller;


import com.game.find.word.KeywordQuiz.dto.KeywordQuizRequestDto;
import com.game.find.word.KeywordQuiz.entity.KeywordQuiz;
import com.game.find.word.KeywordQuiz.service.KeywordQuizService;
import com.game.find.word.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.KeywordQuizCtrl.CTRL)
@RequiredArgsConstructor
public class KeywordQuizController {

    private final KeywordQuizService service;

    /**
     * Retrieves all sentence building game data for a given English level.
     * @param level The English level to filter the sentences by.
     * @return A list of SentenceBuildGame objects.
     */
    @PostMapping("/today")
    public List<KeywordQuiz> getSentenceBuildGame(@RequestBody KeywordQuizRequestDto request) {
        return service.getRandomWGGames(request.getLanguage(), request.getLevel(), request.getCount());
    }
}