package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.model.ENUM.ManagerNotificationType;

import java.time.LocalDateTime;

public record ManagerNotificationResponse(
        Long id,
        ManagerNotificationType type,
        Long bookingId,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
}
