package com.rentgo.car_rental_service.dto.service;

import com.rentgo.car_rental_service.model.ENUM.EmployeeRole;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterEmployeeCommand(
        @NotNull String username,
        @NotNull String password,
        @NotNull String idNumber,
        @NotNull String name,
        @NotNull String email,
        @NotNull String phoneNumber,
        String profilePicture,
        LocalDate dob,
        @NotNull EmployeeRole employeeRole
) {
}
