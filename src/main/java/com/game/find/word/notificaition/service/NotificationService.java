package com.game.find.word.notificaition.service;


import com.game.find.word.notificaition.dto.NotificationRequest;
import com.game.find.word.notificaition.dto.NotificationResponse;
import com.game.find.word.notificaition.entity.Notification;
import com.game.find.word.notificaition.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        // ID'yi null yap, MongoDB yeni bir ObjectId atayacaktır.
        Notification newNotification = convertToEntity(request);
        newNotification.setId(null);
        newNotification.setCreatedAt(LocalDateTime.now());

        Notification saved = repository.save(newNotification);
        return convertToResponse(saved);
    }


    @Transactional
    public NotificationResponse updateNotification(String id, NotificationRequest request) {
        Notification existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bildirim bulunamadı: " + id));

        // Güncellenecek alanlar
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setType(request.getType());

        Notification updated = repository.save(existing);
        return convertToResponse(updated);
    }


    public List<NotificationResponse> findAllNotifications() {
        // En yeni bildirimleri en başta göstermek için sıralama
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public NotificationResponse findNotificationById(String id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bildirim bulunamadı: " + id));
        return convertToResponse(notification);
    }

    @Transactional
    public void deleteNotification(String id) {
        repository.deleteById(id);
    }


    private Notification convertToEntity(NotificationRequest dto) {
        return Notification.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .type(dto.getType())
                .build();
    }

    private NotificationResponse convertToResponse(Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .build();
    }
}