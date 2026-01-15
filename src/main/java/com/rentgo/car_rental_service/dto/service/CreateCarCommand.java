package com.rentgo.car_rental_service.dto.service;

import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateCarCommand(
        @NotBlank Long managerId,
        @NotBlank @Size(max = 20) String family,
        @NotBlank @Size(max = 20) String type,
        @NotNull @Min(4) Integer seats,
        @Size(max = 20) String fuel,
        @NotNull @Positive BigDecimal price,
        String description,
        @Size(max = 255) String picture,
        @NotNull @Size(min = 1) List<@NotBlank String> colors,
        @NotNull CarStatus carStatus
) {
}
