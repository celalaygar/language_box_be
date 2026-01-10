package com.game.find.word.version.controller;

import com.game.find.word.base.util.ApiPaths;
import com.game.find.word.version.entity.AppVersion;
import com.game.find.word.version.service.AppVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.VersionAdminCtrl.CTRL)
public class AppVersionAdminController {

    private final AppVersionService versionService;

    public AppVersionAdminController(AppVersionService versionService) {
        this.versionService = versionService;
    }

    // Tek satır kaydı günceller. Eğer kayıt yoksa, yeni bir kayıt oluşturur.
    @PostMapping
    public ResponseEntity<AppVersion> updateOrCreateVersion(@RequestBody AppVersion version) {
        AppVersion updatedVersion = versionService.updateVersion(version);
        return ResponseEntity.ok(updatedVersion);
    }
}