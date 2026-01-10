package com.game.find.word.VoiceMatch.controller;


import com.game.find.word.VoiceMatch.entity.VoiceMatch;
import com.game.find.word.VoiceMatch.service.VoiceMatchService;
import com.game.find.word.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.VoiceMatchAdminCtrl.CTRL)
@RequiredArgsConstructor
public class VoiceMatchAdminController {

    private final VoiceMatchService wordService;

    // 🔹 Toplu kayıt
    @PostMapping("/bulk")
    public List<VoiceMatch> saveWordsBulk(@RequestBody List<VoiceMatch> list) {
        return wordService.saveAll(list);
    }

    // 🔹 Toplu kayıt
    @GetMapping("/findAll")
    public List<VoiceMatch> findAll() {
        return wordService.findAll();
    }


}
