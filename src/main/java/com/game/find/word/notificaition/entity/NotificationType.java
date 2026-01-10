package com.game.find.word.notificaition.entity;


public enum NotificationType {
    CHALLENGE_COMPLETED("Challenge Tamamlandı"),
    NEW_LEVEL_UNLOCKED("Yeni Seviye Açıldı"),
    DAILY_REMINDER("Günlük Hatırlatma"),
    NEW_WORD_PACK("Yeni Kelime Paketi"),
    STANDART("standart"),
    SYSTEM_UPDATE("Sistem Güncellemesi"),
    FEATURE_UPDATE("Özellik Güncellemesi"),
    SYSTEM_MAINTENANCE("Sistem Bakımı"),
    GENERAL_ANNOUNCEMENT("Genel Duyuru");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}