package com.game.find.word.SentenceCompletion.controller;


import com.game.find.word.SentenceCompletion.entity.SentenceCompletion;
import com.game.find.word.SentenceCompletion.service.SentenceCompletionService;
import com.game.find.word.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SentenceAdminCtrl.CTRL)
@RequiredArgsConstructor
public class SentenceAdminController {

    private final SentenceCompletionService sentenceService;

    @PostMapping("/saveAll")
    public ResponseEntity<List<SentenceCompletion>> saveAll(@RequestBody List<SentenceCompletion> completions) {

        return new ResponseEntity<>(sentenceService.saveAll(completions), HttpStatus.OK);

    }

    @PostMapping("/save")
    public ResponseEntity<SentenceCompletion> save(@RequestBody SentenceCompletion completions) {

        return new ResponseEntity<>(sentenceService.save(completions), HttpStatus.OK);

    }
    @GetMapping("/findAll")
    public ResponseEntity<List<SentenceCompletion>> findAll(@RequestBody SentenceCompletion completions) {

        return new ResponseEntity<>(sentenceService.findAll(), HttpStatus.OK);

    }
}
