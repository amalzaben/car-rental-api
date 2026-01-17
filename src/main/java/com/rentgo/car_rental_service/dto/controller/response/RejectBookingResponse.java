package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.BookingStatus;
import com.rentgo.car_rental_service.model.ENUM.PaymentStatus;

import java.time.LocalDateTime;

public record RejectBookingResponse(
        Long bookingId,
        BookingStatus bookingStatus,
        PaymentStatus paymentStatus,
        LocalDateTime updatedAt
) {
}
