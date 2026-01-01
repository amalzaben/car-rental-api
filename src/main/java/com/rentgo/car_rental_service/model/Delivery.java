package com.rentgo.car_rental_service.model;


import com.rentgo.car_rental_service.model.ENUM.DeliveryStatus;
import com.rentgo.car_rental_service.model.ENUM.DeliveryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name="type", length=10)
    private DeliveryType type;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=20)
    private DeliveryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="completed_at")
    private LocalDateTime completedAt;
}
