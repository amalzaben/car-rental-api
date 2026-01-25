package com.rentgo.car_rental_service.dto.controller.request;

import com.rentgo.car_rental_service.model.ENUM.EmployeeRole;
import com.rentgo.car_rental_service.model.ENUM.UserRole;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterEmployeeRequest(
         String username,
        String password,
         String idNumber,
         String name,
         String email,
        String profilePicture,
        LocalDate dob,
         EmployeeRole employeeRole
) {
}
