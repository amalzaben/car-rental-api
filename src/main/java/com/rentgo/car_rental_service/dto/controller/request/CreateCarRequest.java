package com.rentgo.car_rental_service.dto.controller.request;

import java.math.BigDecimal;
import java.util.List;

public record CreateCarRequest(
        Long managerId,
        String family,
        String type,
        Integer seats,
        String fuel,
        BigDecimal price,
        String description,
        List<String> colors
) {
}
