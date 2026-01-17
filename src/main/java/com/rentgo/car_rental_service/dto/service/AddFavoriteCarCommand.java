package com.rentgo.car_rental_service.dto.service;

import jakarta.validation.constraints.NotNull;

public record AddFavoriteCarCommand(
       @NotNull Long userId,
        @NotNull Long carId
) {
}
