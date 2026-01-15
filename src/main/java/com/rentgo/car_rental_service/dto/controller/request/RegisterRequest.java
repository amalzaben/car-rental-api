package com.rentgo.car_rental_service.dto.controller.request;


import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        String username,
        String profilePicture,
       String password
) {
}
