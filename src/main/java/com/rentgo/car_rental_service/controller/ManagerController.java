package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.response.ManagerNotificationResponse;
import com.rentgo.car_rental_service.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping("/{employeeId}/manager-notifications")
    public ResponseEntity<List<ManagerNotificationResponse>> listManagerNotifications(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(managerService.listManagerNotifications(employeeId));
    }

}
