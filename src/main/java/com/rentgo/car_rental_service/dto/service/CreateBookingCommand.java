package com.rentgo.car_rental_service.dto.service;

import com.rentgo.car_rental_service.model.ENUM.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingCommand(
        @NotNull Long customerId,
        @NotNull Long carId,
        @NotNull String carColor,

        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,

        @NotNull String location,
        @NotNull String phoneNumber,
        @NotNull String personalIdImage,
        @NotNull String drivingLicense,

        @NotNull PaymentMethod paymentMethod
) {
}
