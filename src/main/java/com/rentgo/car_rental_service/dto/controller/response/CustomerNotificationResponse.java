package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.CustomerNotificationType;

import java.time.LocalDateTime;

public record CustomerNotificationResponse(
        Long id,
        Long bookingId,
        String message,
        CustomerNotificationType type,
        boolean isRead,
        LocalDateTime createdAt
) {
}
