package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.request.CreateBookingRequest;
import com.rentgo.car_rental_service.dto.controller.response.AddFavoriteCarResponse;
import com.rentgo.car_rental_service.dto.controller.response.BookingDetailsResponse;
import com.rentgo.car_rental_service.dto.controller.response.BookingResponse;
import com.rentgo.car_rental_service.dto.controller.response.CarResponse;
import com.rentgo.car_rental_service.dto.service.CreateBookingCommand;
import com.rentgo.car_rental_service.mapper.BookingMapper;
import com.rentgo.car_rental_service.model.ENUM.BookingStatus;
import com.rentgo.car_rental_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final BookingMapper bookingMapper;

    @PostMapping("/{userId}/favorites/{carId}")
    public ResponseEntity<AddFavoriteCarResponse> addFavorite(
            @PathVariable Long userId,
            @PathVariable Long carId
    ) {
        AddFavoriteCarResponse res = customerService.addCarToFavorites(userId, carId);

        // If newly created -> 201, if already existed -> 200
        return res.alreadyFavorited()
                ? ResponseEntity.ok(res)
                : ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/{userId}/favorites/{carId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId,
                                               @PathVariable Long carId) {
        customerService.removeFromFavorites(userId, carId);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Page<CarResponse>> listFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(customerService.listFavoriteCars(userId, page, size));
    }

    @PostMapping("/{customerId}/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable Long customerId,
            @RequestBody  CreateBookingRequest request
    ) {
        CreateBookingCommand command =
                bookingMapper.toCommand(customerId, request);

        BookingResponse response = customerService.createBooking(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}/bookings")
    public ResponseEntity<Page<BookingResponse>> listBookings(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BookingStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BookingResponse> result = customerService.listCustomerBookings(customerId, status, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{customerId}/bookings/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBookingDetails(
            @PathVariable Long customerId,
            @PathVariable Long bookingId
    ) {
        BookingDetailsResponse response = customerService.getBookingDetails(customerId, bookingId);
        return ResponseEntity.ok(response);
    }



}
