package com.rentgo.car_rental_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dropoff")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DropOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "dropoff_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "is_assigned", nullable = false)
    private boolean isAssigned = false;

    @Column(name = "assigned_to_employee_id")
    private Long assignedToEmployeeId;


    public DropOff(Booking booking, LocalDateTime date) {
        this.booking = booking;
        this.date = date;
        this.isAssigned = false;
        this.assignedToEmployeeId = null;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public LocalDateTime getDate() { return date; }
    public boolean isAssigned() { return isAssigned; }
    public Long getAssignedToEmployeeId() { return assignedToEmployeeId; }

    public void assignToEmployee(Long employeeId) {
        this.isAssigned = true;
        this.assignedToEmployeeId = employeeId;
    }

    public void unassign() {
        this.isAssigned = false;
        this.assignedToEmployeeId = null;
    }
}