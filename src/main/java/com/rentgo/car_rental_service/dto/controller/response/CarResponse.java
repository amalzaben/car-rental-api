package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.CarStatus;

import java.math.BigDecimal;
import java.util.List;

public record CarResponse(
        Long id,
        String family,
        String type,
        Integer seats,
        String fuel,
        BigDecimal price,
        String description,
        String picture,
        CarStatus status,
        List<String> colors
) {
}
