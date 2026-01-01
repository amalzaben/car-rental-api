package com.rentgo.car_rental_service.model;

import com.rentgo.car_rental_service.model.ENUM.EmployeeRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name="name", length=30)
    private String name;

    @Column(name="email", length=60)
    private String email;

    @Column(name="phone_number", length=20)
    private String phoneNumber;

    @Column(name="dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name="role", length=40)
    private EmployeeRole role;
}
