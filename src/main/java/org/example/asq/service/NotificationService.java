package org.example.asq.service;

import lombok.RequiredArgsConstructor;
import org.example.asq.domain.Notification;
import org.example.asq.domain.User;
import org.example.asq.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notify(User receiver, String type, String message, Long postId) {
        Notification n = new Notification();
        n.setReceiver(receiver);
        n.setType(type);
        n.setMessage(message);
        n.setPostId(postId);
        notificationRepository.save(n);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByReceiverIdAndReadFalse(userId);
    }

    @Transactional(readOnly = true)
    public Page<Notification> findAll(Long userId, Pageable pageable) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllRead(userId);
    }
}
