package com.example.wallet.service.extra;

import com.example.wallet.model.extra.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
//public class NotificationService {
//
//    private final NotificationRepository notificationRepository;
//
//    @Autowired
//    public NotificationService(NotificationRepository notificationRepository) {
//        this.notificationRepository = notificationRepository;
//    }
//
//    public List<Notification> getAllNotifications() {
//        return notificationRepository.findAll();
//    }
//
//    public Notification saveNotification(Notification notification) {
//        return notificationRepository.save(notification);
//    }
//
//    // Additional methods as needed based on your requirements
//
//    // For example, you might want to implement a method to retrieve notifications for a specific user
//    public List<Notification> getNotificationsByUserId(Long userId) {
//        return notificationRepository.findByUserId(userId);
//    }
//}
