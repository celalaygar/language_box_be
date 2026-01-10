package com.game.find.word.KeywordQuiz.controller;


import com.game.find.word.KeywordQuiz.dto.KeywordQuizRequestDto;
import com.game.find.word.KeywordQuiz.entity.KeywordQuiz;
import com.game.find.word.KeywordQuiz.service.KeywordQuizService;
import com.game.find.word.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.KeywordQuizAdminCtrl.CTRL)
@RequiredArgsConstructor
public class KeywordQuizAdminController {

    private final KeywordQuizService service;


    @PostMapping("/save")
    public KeywordQuiz save(@RequestBody KeywordQuiz request) {
        return service.save(request);
    }

    @PostMapping("/saveAll")
    public List<KeywordQuiz> saveAll(@RequestBody List<KeywordQuiz> request) {
        return service.saveAll(request);
    }

    @GetMapping("/findAll")
    public List<KeywordQuiz> findAll() {
        return service.findAll();
    }
}