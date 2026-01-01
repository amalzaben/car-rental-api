package com.rentgo.car_rental_service.model;

import com.rentgo.car_rental_service.model.ENUM.ManagerNotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_notifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable=false, length=30)
    private ManagerNotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name="message", columnDefinition="TEXT")
    private String message;

    @Column(name="is_read", nullable=false)
    private boolean isRead;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
