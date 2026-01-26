package com.rentgo.car_rental_service.dto;

public record BookingReport(
        long accepted,
        long canceled,
        long requested
) {
}
