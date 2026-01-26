package com.rentgo.car_rental_service.dto.controller.response;

import com.rentgo.car_rental_service.dto.BookingReport;
import com.rentgo.car_rental_service.dto.CarsReport;
import com.rentgo.car_rental_service.dto.ProfitReport;

public record MonthlyReportResponse(
        int year,
        int month,
        CarsReport cars,
        ProfitReport profits,
        BookingReport bookings
) {
}
