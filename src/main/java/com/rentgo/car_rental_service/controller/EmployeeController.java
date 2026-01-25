package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.request.ApproveBookingRequest;
import com.rentgo.car_rental_service.dto.controller.request.ManualCreateBookingRequest;
import com.rentgo.car_rental_service.dto.controller.request.RejectBookingRequest;
import com.rentgo.car_rental_service.dto.controller.response.ApproveBookingResponse;
import com.rentgo.car_rental_service.dto.controller.response.BookingResponse;
import com.rentgo.car_rental_service.dto.controller.response.PendingBookingResponse;
import com.rentgo.car_rental_service.dto.controller.response.RejectBookingResponse;
import com.rentgo.car_rental_service.dto.service.ManualCreateBookingCommand;
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

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ManualBookingMapper manualBookingMapper;

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



}
