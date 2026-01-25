package com.rentgo.car_rental_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pickup")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PickUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "pickup_date", nullable = false)
    private LocalDateTime date;


    public PickUp(Booking booking, LocalDateTime date) {
        this.booking = booking;
        this.date = date;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public LocalDateTime getDate() { return date; }
}