package com.rentgo.car_rental_service.mapper;

import com.rentgo.car_rental_service.dto.controller.request.CreateCarRequest;
import com.rentgo.car_rental_service.dto.controller.response.CarListItemResponse;
import com.rentgo.car_rental_service.dto.controller.response.CarResponse;
import com.rentgo.car_rental_service.dto.service.CreateCarCommand;
import com.rentgo.car_rental_service.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CreateCarCommand toCommand(CreateCarRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(CarStatus.available)")
    Car toEntity(CreateCarCommand command);

    @Mapping(target = "colors", ignore = true) // we set manually
    CarResponse toResponse(Car car);

    CarListItemResponse toListItem(Car car);
}
