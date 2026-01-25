package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.request.ApproveBookingRequest;
import com.rentgo.car_rental_service.dto.controller.request.ManualCreateBookingRequest;
import com.rentgo.car_rental_service.dto.controller.request.RejectBookingRequest;
import com.rentgo.car_rental_service.dto.controller.request.UpdateEmployeeRequest;
import com.rentgo.car_rental_service.dto.controller.response.*;
import com.rentgo.car_rental_service.dto.service.ManualCreateBookingCommand;
import com.rentgo.car_rental_service.dto.service.UpdateEmployeeCommand;
import com.rentgo.car_rental_service.mapper.EmployeeMapper;
import com.rentgo.car_rental_service.mapper.ManualBookingMapper;
import com.rentgo.car_rental_service.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ManualBookingMapper manualBookingMapper;
    private final EmployeeMapper employeeMapper;

    @PatchMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> updateEmployeeInfo(
            @PathVariable Long employeeId,
            @RequestBody UpdateEmployeeRequest request
    ) {
        UpdateEmployeeCommand cmd = employeeMapper.toUpdateCommand(employeeId, request);
        EmployeeResponse response = employeeService.updateEmployeeInfo(cmd);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{employeeId}/bookings/pending")
    public ResponseEntity<Page<PendingBookingResponse>> listPendingBookings(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(employeeService.listPendingBookings(employeeId, pageable));
    }

    @PatchMapping("/{employeeId}/bookings/{bookingId}/reject")
    public ResponseEntity<RejectBookingResponse> rejectBooking(
            @PathVariable Long employeeId,
            @PathVariable Long bookingId,
            @RequestBody(required = false) RejectBookingRequest request
    ) {
        String reason = (request == null) ? null : request.reason();
        RejectBookingResponse response = employeeService.rejectBooking(employeeId, bookingId, reason);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{employeeId}/bookings/{bookingId}/approve")
    public ResponseEntity<ApproveBookingResponse> approveBooking(
            @PathVariable Long employeeId,
            @PathVariable Long bookingId,
            @RequestBody(required = false) ApproveBookingRequest request
    ) {
        String note = (request == null) ? null : request.note();
        ApproveBookingResponse response = employeeService.approveBooking(employeeId, bookingId, note);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{employeeId}/bookings/manual")
    public ResponseEntity<BookingResponse> manuallyCreateBooking(
            @PathVariable Long employeeId,
            @RequestBody ManualCreateBookingRequest request
    ) {
        ManualCreateBookingCommand cmd = manualBookingMapper.toCommand(employeeId, request);
        BookingResponse response = employeeService.manuallyCreateBooking(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{employeeId}/deliveries/today")
    public ResponseEntity<List<DueDeliveryResponse>> getTodayDeliveries(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeService.getTodayUnassignedDeliveries(employeeId));
    }

    @PostMapping("/{employeeId}/pickups/{pickupId}/assign")
    public ResponseEntity<DueDeliveryResponse> assignPickup(
            @PathVariable Long employeeId,
            @PathVariable Long pickupId
    ) {
        return ResponseEntity.ok(employeeService.assignPickup(employeeId, pickupId));
    }
    @PostMapping("/{employeeId}/dropoffs/{dropoffId}/assign")
    public ResponseEntity<DueDeliveryResponse> assignDropoff(
            @PathVariable Long employeeId,
            @PathVariable Long dropoffId
    ) {
        return ResponseEntity.ok(employeeService.assignDropoff(employeeId, dropoffId));
    }

    @GetMapping("/{employeeId}/deliveries/assigned")
    public ResponseEntity<List<DueDeliveryResponse>> getAssignedDeliveries(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeService.getAssignedDeliveries(employeeId));
    }






}
