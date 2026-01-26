package com.rentgo.car_rental_service.dto.service;

import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateCarCommand(
        @NotNull Long managerId,
        @NotBlank String family,
        @NotBlank String type,
        @NotNull Integer seats,
        @NotBlank String fuel,
        @NotNull BigDecimal price,
        String description,
        @NotNull byte[] pictureData,
        @NotBlank String pictureContentType,
        List<String> colors,
        @NotNull CarStatus status
) {
}
