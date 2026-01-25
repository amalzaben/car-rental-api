package com.rentgo.car_rental_service.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResetPasswordCommand{
        @NotNull
        private Long userId;

        @NotBlank
        private String oldPassword;

        @NotBlank
        @Size(min = 6)
        private String newPassword;
}
