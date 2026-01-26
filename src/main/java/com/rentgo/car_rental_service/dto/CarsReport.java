package com.rentgo.car_rental_service.dto;

public record CarsReport(
        long booked,
        long inMaintenance,
        long available
) {
}
