package com.rentgo.car_rental_service.mapper;

import com.rentgo.car_rental_service.dto.controller.request.CreateBookingRequest;
import com.rentgo.car_rental_service.dto.controller.response.BookingDetailsResponse;
import com.rentgo.car_rental_service.dto.controller.response.PendingBookingResponse;
import com.rentgo.car_rental_service.dto.service.CreateBookingCommand;
import com.rentgo.car_rental_service.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    CreateBookingCommand toCommand(
            Long customerId,
            CreateBookingRequest request
    );
    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "status", source = "status")

    // Car
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "carFamily", source = "car.family")
    @Mapping(target = "carType", source = "car.type")
    @Mapping(target = "pricePerDay", source = "car.price")

    // Color name (from carColor -> color -> name)
    @Mapping(target = "color", source = "carColor.color.name")

    // Dates & location
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "location", source = "location")

    // Identity (your entity field is personalId but DTO wants personalIdImage)
    @Mapping(target = "personalIdImage", source = "personalId")
    @Mapping(target = "drivingLicense", source = "drivingLicense")

    // Payment
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "paymentMethod", source = "payment.method")
    @Mapping(target = "paymentStatus", source = "payment.status")
    @Mapping(target = "totalPrice", source = "totalPrice")

    @Mapping(target = "createdAt", source = "createdAt")
    BookingDetailsResponse toDetailsResponse(Booking booking);


    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "carId", source = "car.id")
    PendingBookingResponse toPendingBookingResponse(Booking booking);
}
