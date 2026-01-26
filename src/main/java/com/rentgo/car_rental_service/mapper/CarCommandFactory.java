package com.rentgo.car_rental_service.mapper;


import com.rentgo.car_rental_service.dto.controller.request.CreateCarRequest;
import com.rentgo.car_rental_service.dto.service.CreateCarCommand;
import com.rentgo.car_rental_service.exception.ConflictException;
import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class CarCommandFactory {

    public CreateCarCommand toCreateCommand(CreateCarRequest request, MultipartFile picture) {
        if (picture == null || picture.isEmpty()) {
            throw new ConflictException("Car picture is required");
        }

        try {
            return new CreateCarCommand(
                    request.managerId(),
                    request.family(),
                    request.type(),
                    request.seats(),
                    request.fuel(),
                    request.price(),
                    request.description(),
                    picture.getBytes(),
                    picture.getContentType(),
                    request.colors(),
                    CarStatus.available
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded image", e);
        }
    }
}
