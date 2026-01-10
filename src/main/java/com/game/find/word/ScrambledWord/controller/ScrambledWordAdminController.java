package com.game.find.word.ScrambledWord.controller;


import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.ScrambledWord.dto.ScrambledWordDto;
import com.game.find.word.ScrambledWord.entity.ScrambledWord;
import com.game.find.word.ScrambledWord.service.ScrambledWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ScrambledWordAdminCtrl.CTRL)
@RequiredArgsConstructor
public class ScrambledWordAdminController {

    private final ScrambledWordService wordService;

    // 🔹 Toplu kayıt
    @PostMapping("/bulk")
    public ScrambledWord saveWordsBulk(@RequestBody ScrambledWordDto requestDtos) {
        return wordService.saveWordsBulk(requestDtos);
    }

    // 🔹 Toplu kayıt
    @GetMapping("/findAll")
    public List<ScrambledWord> findAll() {
        return wordService.findAll();
    }


}
