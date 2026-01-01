package com.rentgo.car_rental_service.model;

import com.rentgo.car_rental_service.model.ENUM.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="username", nullable=false, length=25, unique=true)
    private String username;

    @Column(name="password", nullable=false, length=255)
    private String password;

    @Column(name="profile_picture", length=255)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable=false, length=20)
    private UserRole role;

    @Column(name="created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
