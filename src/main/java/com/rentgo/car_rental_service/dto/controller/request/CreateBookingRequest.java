package com.rentgo.car_rental_service.dto.controller.request;

import com.rentgo.car_rental_service.model.ENUM.PaymentMethod;

import java.time.LocalDate;

public record CreateBookingRequest(
        Long carId,
        String carColor,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String phoneNumber,
        String personalIdImage,
        String drivingLicense,
        PaymentMethod paymentMethod
) {
}
