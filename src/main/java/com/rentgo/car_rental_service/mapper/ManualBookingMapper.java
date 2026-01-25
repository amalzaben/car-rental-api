package com.rentgo.car_rental_service.mapper;

import com.rentgo.car_rental_service.dto.controller.request.ManualCreateBookingRequest;
import com.rentgo.car_rental_service.dto.service.ManualCreateBookingCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManualBookingMapper {
    ManualCreateBookingCommand toCommand(Long employeeId, ManualCreateBookingRequest request);
}
