package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.CarStatus;

import java.math.BigDecimal;

public record CarListItemResponse(
        Long id,
        String family,
        String type,
        Integer seats,
        String fuel,
        BigDecimal price,
        String picture,
        CarStatus status
) {
}
