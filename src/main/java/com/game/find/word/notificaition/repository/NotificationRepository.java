package com.game.find.word.notificaition.repository;


import com.game.find.word.notificaition.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // İstenen CRUD metotları (findById, findAll, save, deleteById) MongoRepository'den miras alınmıştır.

    // Ek: Bildirim tipine göre liste çekme metodu
    List<Notification> findByType(String type);

    // Eğer createdDate'e göre sıralı listelenmesini istiyorsak
    List<Notification> findAllByOrderByCreatedAtDesc();
}