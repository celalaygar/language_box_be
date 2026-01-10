package com.game.find.word.notificaition.dto;

import com.game.find.word.notificaition.entity.NotificationType;
import lombok.Data;

@Data
public class NotificationRequest {
    private String id;
    private String title;
    private String description;
    private NotificationType type;
}
