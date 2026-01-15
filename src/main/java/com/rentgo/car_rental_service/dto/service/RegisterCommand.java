package com.rentgo.car_rental_service.dto.service;

import jakarta.validation.constraints.NotNull;

public record RegisterCommand(
        @NotNull String username,
        String profilePicture,
        @NotNull String password
){

}
