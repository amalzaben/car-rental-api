package com.rentgo.car_rental_service.service;

import com.rentgo.car_rental_service.dto.controller.request.ResetPasswordRequest;
import com.rentgo.car_rental_service.dto.controller.response.LoginResponse;
import com.rentgo.car_rental_service.dto.controller.response.RegisterEmployeeResponse;
import com.rentgo.car_rental_service.dto.service.LoginCommand;
import com.rentgo.car_rental_service.dto.service.RegisterCommand;
import com.rentgo.car_rental_service.dto.service.RegisterEmployeeCommand;
import com.rentgo.car_rental_service.exception.ConflictException;
import com.rentgo.car_rental_service.exception.UnauthenticatedException;
import com.rentgo.car_rental_service.model.ENUM.UserRole;
import com.rentgo.car_rental_service.model.Employee;
import com.rentgo.car_rental_service.model.User;
import com.rentgo.car_rental_service.repository.EmployeeRepository;
import com.rentgo.car_rental_service.repository.UserRepository;
import com.rentgo.car_rental_service.util.PasswordUtil;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;



    public User signup(@Valid RegisterCommand registerUserCommand) {

        try {
            User user = User.builder()
                    .username(registerUserCommand.username())
                    .password(PasswordUtil.encode(registerUserCommand.password()))
                    .profilePicture(registerUserCommand.profilePicture())
                    .role(UserRole.customer)
                    .build();
            return userRepository.save(user);
        }catch(DataIntegrityViolationException ex){
            throw new ConflictException("Username Already Exists!");
        }
    }

    public LoginResponse login(@Valid LoginCommand loginCommand) {

        User user = userRepository.findByUsername(loginCommand.username())
                .orElseThrow(() -> new UnauthenticatedException("Invalid credentials"));

        if (!PasswordUtil.matches(loginCommand.password(), user.getPassword())) {
            throw new UnauthenticatedException("Invalid credentials");
        }

        if (user.getRole() == UserRole.employee) {
            Employee employee = employeeRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ConflictException(
                            "Employee information missing for this user"));
            return new LoginResponse(
                    user.getRole(),
                    user.getId(),
                    user.getUsername(),
                    employee.getId(),
                    user.getProfilePicture(),
                    employee.getRole(),
                    employee.getName(),
                    employee.getEmail(),
                    employee.getDob()
            );

        }
        return new LoginResponse(
                user.getRole(),
                user.getId(),
                user.getUsername(),
                null,
                user.getProfilePicture(),
                null,
                null,
                null,
                null
        );
    }

    @Transactional
    public RegisterEmployeeResponse registerEmployee(
            @Valid RegisterEmployeeCommand registerEmployeeCommand){
        User user=null;
        try {
             user = User.builder()
                    .username(registerEmployeeCommand.username())
                    .password(PasswordUtil.encode(registerEmployeeCommand.password()))
                    .profilePicture(registerEmployeeCommand.profilePicture())
                    .role(UserRole.employee)
                    .build();
             userRepository.save(user);
        }catch(DataIntegrityViolationException ex){
            throw new ConflictException("Username Already Exists!");
        }

        Employee employee= Employee.builder()
                .user(user)
                .role(registerEmployeeCommand.employeeRole())
                .name(registerEmployeeCommand.name())
                .phoneNumber(registerEmployeeCommand.phoneNumber())
                .email(registerEmployeeCommand.email())
                .dob(registerEmployeeCommand.dob())
                .build();
        employeeRepository.save(employee);
        return new RegisterEmployeeResponse(
                user.getId(),
                employee.getId(),
                user.getRole(),
                employee.getRole(),
                user.getUsername(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                user.getProfilePicture(),
                employee.getDob()
        );
    }

    @Transactional
    public void resetPassword(Long userId, ResetPasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Verify old password
        if (!user.getPassword().equals(request.oldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // 2. Prevent same password
        if (request.oldPassword().equals(request.newPassword())) {
            throw new IllegalArgumentException("New password must be different");
        }

        // 3. Update
        user.setPassword(request.newPassword());
        userRepository.save(user);
    }

}
