package com.rentgo.car_rental_service.dto.controller.response;


import com.rentgo.car_rental_service.model.ENUM.EmployeeRole;
import com.rentgo.car_rental_service.model.ENUM.UserRole;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record LoginResponse(

        UserRole role,
        Long userId,
        String username,
        Long employeeId,
        String profilePicture,
        EmployeeRole employeeRole,
        String name,
        String email,
        LocalDate dob
) {
}
