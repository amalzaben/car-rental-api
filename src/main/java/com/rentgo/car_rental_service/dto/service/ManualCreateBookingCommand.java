package com.rentgo.car_rental_service.dto.service;

import com.rentgo.car_rental_service.model.ENUM.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ManualCreateBookingCommand(
        @NotNull Long employeeId,
        @NotNull Long customerId,
        @NotNull Long carId,
        @NotBlank String carColor,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotBlank String location,
        @NotBlank String phoneNumber,
        @NotBlank String personalIdImage,
        @NotBlank String drivingLicense,
        @NotNull PaymentMethod paymentMethod
) {
}
