package com.rentgo.car_rental_service.mapper;

import com.rentgo.car_rental_service.dto.controller.request.LoginRequest;
import com.rentgo.car_rental_service.dto.controller.request.RegisterEmployeeRequest;
import com.rentgo.car_rental_service.dto.service.LoginCommand;
import com.rentgo.car_rental_service.dto.service.RegisterCommand;
import com.rentgo.car_rental_service.dto.controller.request.RegisterRequest;
import com.rentgo.car_rental_service.dto.controller.response.RegisterResponse;
import com.rentgo.car_rental_service.dto.service.RegisterEmployeeCommand;
import com.rentgo.car_rental_service.model.User;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    public RegisterResponse toRegisterResponse(User user);
    public RegisterCommand toRegisterCommand(RegisterRequest registerUserRequest);
    public LoginCommand toLoginCommand(LoginRequest loginRequest);
    public RegisterEmployeeCommand toRegisterEmployeeCommand(RegisterEmployeeRequest registerEmployeeRequest);
}
