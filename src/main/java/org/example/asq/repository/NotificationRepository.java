package org.example.asq.repository;

import org.example.asq.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByReceiverIdAndReadFalse(Long receiverId);
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    List<Notification> findByReceiverIdAndReadFalse(Long receiverId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.receiver.id = :receiverId")
    void markAllRead(Long receiverId);
}
