package com.rentgo.car_rental_service.model;

import com.rentgo.car_rental_service.model.ENUM.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_color_id", nullable = false)
    private CarColor carColor;

    @Column(name="start_date", nullable=false)
    private LocalDate startDate;

    @Column(name="end_date", nullable=false)
    private LocalDate endDate;

    @Column(name="location", nullable=false, length=60)
    private String location;

    @Column(name="phone_number", nullable=false, length=25)
    private String phoneNumber;

    @Column(name="personal_id", length=30)
    private String personalId;

    @Column(name="driving_license", length=30)
    private String drivingLicense;

    @Column(name="total_price", precision=10, scale=2)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false, length=20)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_employee_id")
    private Employee handledByEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_manually_by_employee_id")
    private Employee addedManuallyByEmployee;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
