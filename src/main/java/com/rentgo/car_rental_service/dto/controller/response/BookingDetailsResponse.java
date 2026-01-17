package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.BookingStatus;
import com.rentgo.car_rental_service.model.ENUM.PaymentMethod;
import com.rentgo.car_rental_service.model.ENUM.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingDetailsResponse(
        Long bookingId,
        BookingStatus status,

        // Car
        Long carId,
        String carFamily,
        String carType,
        BigDecimal pricePerDay,
        String color,

        // Dates & location
        LocalDate startDate,
        LocalDate endDate,
        String location,

        // Identity
        String personalIdImage,
        String drivingLicense,

        // Payment
        Long paymentId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        BigDecimal totalPrice,

        LocalDateTime createdAt
) {
}
