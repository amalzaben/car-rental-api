package com.rentgo.car_rental_service.dto;

import java.math.BigDecimal;

public record ProfitReport(
        BigDecimal totalProfit,
        long rentedCars
) {
}
