package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.response.ManagerNotificationResponse;
import com.rentgo.car_rental_service.dto.controller.response.MonthlyReportResponse;
import com.rentgo.car_rental_service.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping("/{userId}/manager-notifications")
    public ResponseEntity<List<ManagerNotificationResponse>> listManagerNotifications(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(managerService.listManagerNotifications(userId));
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(managerService.getMonthlyReport(year, month));
    }


}
