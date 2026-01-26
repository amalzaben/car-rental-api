package com.rentgo.car_rental_service.dto.controller.response;

public record CarPictureResponse(
        byte[] data,
        String contentType
) {
}
