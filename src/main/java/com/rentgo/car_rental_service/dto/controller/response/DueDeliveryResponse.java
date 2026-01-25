package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.DeliveryType;

import java.time.LocalDateTime;

public record DueDeliveryResponse(
        Long deliveryId,
        Long bookingId,

        DeliveryType type,
        LocalDateTime dueDate,

        String location,
        String phoneNumber,

        String carImageUrl,
        String carType,
        String carFamily
) {
}
