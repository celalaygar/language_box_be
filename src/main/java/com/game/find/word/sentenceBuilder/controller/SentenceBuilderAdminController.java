package com.game.find.word.sentenceBuilder.controller;


import com.game.find.word.ScrambledWord.dto.WordPageRequestDto;
import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.sentenceBuilder.entity.SentenceBuildGame;
import com.game.find.word.sentenceBuilder.service.SentenceBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SentenceBuilderAdminCtrl.CTRL)
@RequiredArgsConstructor
public class SentenceBuilderAdminController {

    private final SentenceBuilderService sentenceBuilderService;

    @GetMapping("/findAll")
    public List<SentenceBuildGame> findAll() {
        return sentenceBuilderService.findAll();
    }
    @PostMapping("/save")
    public SentenceBuildGame save(@RequestBody SentenceBuildGame game) {
        return sentenceBuilderService.save(game);
    }
    @PostMapping("/saveAll")
    public List<SentenceBuildGame> saveAll(@RequestBody List<SentenceBuildGame> list) {
        return sentenceBuilderService.saveAll(list);
    }
}