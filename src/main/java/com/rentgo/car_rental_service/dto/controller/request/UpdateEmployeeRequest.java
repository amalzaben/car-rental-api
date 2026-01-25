package com.rentgo.car_rental_service.dto.controller.request;

import java.time.LocalDate;

public record UpdateEmployeeRequest(
        String idNumber,
        String name,
        String email,
        LocalDate dob
) {
}
