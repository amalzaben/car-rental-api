package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.EmployeeRole;
import com.rentgo.car_rental_service.model.ENUM.UserRole;

import javax.management.relation.Role;
import java.time.LocalDate;

public record RegisterEmployeeResponse(
        Long userId,
        Long employeeId,
        UserRole role,
        EmployeeRole employeeRole,
        String username,
        String idNumber,
        String name,
        String email,
        String profilePicture,
        LocalDate dob
) {
}
