package com.rentgo.car_rental_service.dto.controller.response;

public record AddFavoriteCarResponse(
        Long userId,
        Long carId,
        boolean alreadyFavorited
) {
}
