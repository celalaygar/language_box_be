package com.game.find.word.notificaition.dto;


import com.game.find.word.notificaition.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private LocalDateTime createdAt;
    private String title;
    private String description;
    private NotificationType type;

}