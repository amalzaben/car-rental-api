package com.rentgo.car_rental_service.model;

import com.rentgo.car_rental_service.model.ENUM.PaymentMethod;
import com.rentgo.car_rental_service.model.ENUM.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="amount", nullable=false, precision=10, scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="method", nullable=false, length=20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=20)
    private PaymentStatus status;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
