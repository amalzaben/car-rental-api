package com.rentgo.car_rental_service.dto.controller.request;

public record ResetPasswordRequest(
        String oldPassword,
        String newPassword
) {
}
