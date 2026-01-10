package com.game.find.word.GridChallenge.controller;


import com.game.find.word.GridChallenge.entity.GridChallenge;
import com.game.find.word.GridChallenge.dto.GridChallengeCreateRequest;
import com.game.find.word.GridChallenge.service.GridChallengeService;
import com.game.find.word.KeywordQuiz.dto.KeywordQuizRequestDto;
import com.game.find.word.base.model.EnglishLevel;
import com.game.find.word.base.model.Language;
import com.game.find.word.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.GridChallangeCtrl.CTRL)
@RequiredArgsConstructor
public class GridChallengeController {

    private final GridChallengeService service;


    @PostMapping("/saveAll")
    public ResponseEntity<List<GridChallenge>> saveAll(
            @RequestBody List<GridChallengeCreateRequest> requests) {
        List<GridChallenge> savedChallenges = service.saveAllChallenges(requests);
        return new ResponseEntity<>(savedChallenges, HttpStatus.CREATED);
    }

    @PostMapping("/today")
    public ResponseEntity<List<GridChallenge>> getChallenges(
            @RequestBody KeywordQuizRequestDto request) {
        List<GridChallenge> challenges = service.getRandom(request.getLevel(), request.getLanguage(), request.getCount());
        return ResponseEntity.ok(challenges);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        service.deleteChallengeById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}