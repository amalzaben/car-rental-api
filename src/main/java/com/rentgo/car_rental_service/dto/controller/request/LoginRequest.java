package com.rentgo.car_rental_service.dto.controller.request;

public record LoginRequest(
        String username,
        String password
) {
}
