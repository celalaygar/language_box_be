package com.game.find.word.version.controller;

import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.version.entity.AppVersion;
import com.game.find.word.version.service.AppVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.VersionCtrl.CTRL)
public class AppVersionController {

    private final AppVersionService versionService;

    public AppVersionController(AppVersionService versionService) {
        this.versionService = versionService;
    }

    // Tek satır kaydı getirir. Eğer kayıt yoksa, oluşturur.
    @GetMapping
    public ResponseEntity<AppVersion> getVersion() {
        AppVersion version = versionService.getOrCreateVersion();
        return ResponseEntity.ok(version);
    }

}