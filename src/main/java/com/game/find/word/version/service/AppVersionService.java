package com.game.find.word.version.service;

import com.game.find.word.version.entity.AppVersion;
import com.game.find.word.version.repository.AppVersionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AppVersionService {

    private final AppVersionRepository versionRepository;

    public AppVersionService(AppVersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    @PostConstruct
    public void initializeZodiacSigns() {
        Optional<AppVersion> versionOptional = versionRepository.findAll().stream().findFirst();

        if (!versionOptional.isPresent()) {
            AppVersion first = new AppVersion();
            first.setVersionNumber("1.0.0");
            first.setControl(true);
            first.setCreatedDate(new Date());
            versionRepository.save(first);
        }
    }



    public AppVersion getOrCreateVersion() {
        Optional<AppVersion> versionOptional = versionRepository.findAll().stream().findFirst();

        if (versionOptional.isPresent()) {
            return versionOptional.get();
        }
        return null;
    }

    public AppVersion updateVersion(AppVersion updatedVersion) {
        Optional<AppVersion> versionOptional = versionRepository.findAll().stream().findFirst();

        if (versionOptional.isPresent()) {
            AppVersion existingVersion = versionOptional.get();
            existingVersion.setVersionNumber(updatedVersion.getVersionNumber());
            existingVersion.setControl(updatedVersion.getControl());
            updatedVersion.setCreatedDate(new Date());

            return versionRepository.save(existingVersion);
        } else {
            // Eğer kayıt yoksa, yeni bir kayıt oluştur
            // Bu, hem güncelleme hem de oluşturma işlemini tek bir metotta sağlar
            updatedVersion.setCreatedDate(new Date());
            return versionRepository.save(updatedVersion);
        }
    }
}