package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.EmployeeRole;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        Long userId,
        String idNumber,
        String name,
        String email,
        LocalDate dob,
        EmployeeRole role
) {
}
