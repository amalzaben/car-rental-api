package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.UserRole;

public record RegisterResponse(
    Long id,
    String username,
    UserRole role,
    String profilePicture
) {
}
