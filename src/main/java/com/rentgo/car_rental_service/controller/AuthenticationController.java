package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.request.LoginRequest;
import com.rentgo.car_rental_service.dto.controller.request.RegisterEmployeeRequest;
import com.rentgo.car_rental_service.dto.controller.request.RegisterRequest;
import com.rentgo.car_rental_service.dto.controller.request.ResetPasswordRequest;
import com.rentgo.car_rental_service.dto.controller.response.LoginResponse;
import com.rentgo.car_rental_service.dto.controller.response.RegisterEmployeeResponse;
import com.rentgo.car_rental_service.dto.controller.response.RegisterResponse;
import com.rentgo.car_rental_service.mapper.AuthenticationMapper;
import com.rentgo.car_rental_service.model.User;
import com.rentgo.car_rental_service.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class  AuthenticationController {


    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationMapper authenticationMapper;


    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerUserRequest ) {
        User registeredUser = authenticationService.signup(
                authenticationMapper.toRegisterCommand(registerUserRequest));
        return ResponseEntity.ok(authenticationMapper.toRegisterResponse(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse=authenticationService.login(
                authenticationMapper.toLoginCommand(loginRequest)
        );
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup/employee")
    public ResponseEntity<RegisterEmployeeResponse> registerEmployee(
            @RequestBody RegisterEmployeeRequest registerEmployeeRequest) {
        RegisterEmployeeResponse registerEmployeeResponse=authenticationService.registerEmployee
                (authenticationMapper.toRegisterEmployeeCommand(registerEmployeeRequest));
        return ResponseEntity.ok(registerEmployeeResponse);
    }

    @PatchMapping("/{userId}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long userId,
            @RequestBody ResetPasswordRequest request
    ) {
        authenticationService.resetPassword(userId, request);
        return ResponseEntity.noContent().build();
    }

}

