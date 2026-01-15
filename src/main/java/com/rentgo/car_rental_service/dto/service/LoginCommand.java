package com.rentgo.car_rental_service.dto.service;

import jakarta.validation.constraints.NotNull;

public record LoginCommand(
        @NotNull String username,
        @NotNull String password
) {
}
