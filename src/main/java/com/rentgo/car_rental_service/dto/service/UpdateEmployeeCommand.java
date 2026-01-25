package com.rentgo.car_rental_service.dto.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeCommand {

    @NotNull
    private Long employeeId;

    @Size(max = 30)
    private String idNumber;

    @Size(max = 30)
    private String name;

    @Email
    @Size(max = 60)
    private String email;

    private LocalDate dob;
}
