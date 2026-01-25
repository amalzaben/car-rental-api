package com.rentgo.car_rental_service.service;

import com.rentgo.car_rental_service.dto.controller.response.CarListItemResponse;
import com.rentgo.car_rental_service.dto.controller.response.CarResponse;
import com.rentgo.car_rental_service.dto.service.CreateCarCommand;
import com.rentgo.car_rental_service.exception.ForbiddenException;
import com.rentgo.car_rental_service.exception.ResourceNotFoundException;
import com.rentgo.car_rental_service.exception.UnauthenticatedException;
import com.rentgo.car_rental_service.mapper.CarMapper;
import com.rentgo.car_rental_service.model.Car;
import com.rentgo.car_rental_service.model.CarColor;
import com.rentgo.car_rental_service.model.Color;
import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import com.rentgo.car_rental_service.model.ENUM.UserRole;
import com.rentgo.car_rental_service.repository.CarColorRepository;
import com.rentgo.car_rental_service.repository.CarRepository;
import com.rentgo.car_rental_service.repository.ColorRepository;
import com.rentgo.car_rental_service.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CarService {


    private final CarRepository carRepository;
    private final ColorRepository colorRepository;
    private final CarColorRepository carColorRepository;
    private final CarMapper carMapper;
    private final UserRepository userRepository;

    public CarService(
            CarRepository carRepository,
            ColorRepository colorRepository,
            CarColorRepository carColorRepository,
            CarMapper carMapper,
            UserRepository userRepository
    ) {
        this.carRepository = carRepository;
        this.colorRepository = colorRepository;
        this.carColorRepository = carColorRepository;
        this.carMapper = carMapper;
        this.userRepository= userRepository;
    }

    @Transactional
    public CarResponse addNewCar(@Valid CreateCarCommand command) {

        validateManager(command.managerId());

        // Normalize colors: trim + lower
        List<String> normalizedColors = command.colors().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .distinct()
                .toList();

        Car car = carMapper.toEntity(new CreateCarCommand(
                command.managerId(),command.family(), command.type(), command.seats(),
                command.fuel(), command.price(), command.description(),
                command.picture(), normalizedColors, CarStatus.available
        ));

        Car savedCar = carRepository.save(car);

        // Resolve/create colors + create join rows
        // (عشان مش ضروري كل ما بدي اضيف لون جديد اعدل في ايدي على الداتا بيس)
        List<Color> resolvedColors = new ArrayList<>();
        for (String colorName : normalizedColors) {
            Color color = colorRepository.findByName(colorName)
                    .orElseGet(() -> colorRepository.save(
                            Color.builder().name(colorName).build()
                    ));
            resolvedColors.add(color);

            CarColor join = CarColor.builder()
                    .car(savedCar)
                    .color(color)
                    .build();
            carColorRepository.save(join);
        }

        return new CarResponse(
                savedCar.getId(),
                savedCar.getFamily(),
                savedCar.getType(),
                savedCar.getSeats(),
                savedCar.getFuel(),
                savedCar.getPrice(),
                savedCar.getDescription(),
                savedCar.getPicture(),
                savedCar.getStatus(),
                resolvedColors.stream().map(Color::getName).toList()
        );
    }

    public CarResponse getCarDetails(Long carId) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        List<String> colors = carColorRepository.findColorNamesByCarId(carId);

        CarResponse response = carMapper.toResponse(car);

        return new CarResponse(
                response.id(),
                response.family(),
                response.type(),
                response.seats(),
                response.fuel(),
                response.price(),
                response.description(),
                response.picture(),
                response.status(),
                colors
        );
    }

    public Page<CarListItemResponse> getCars(
            String q,
            String type,
            String color,
            Integer seats,
            String fuel,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            CarStatus status,
            Pageable pageable
    ) {
        return carRepository.searchCars(q, type, color, seats, fuel, minPrice, maxPrice, status, pageable)
                .map(carMapper::toListItem);
    }

    //helper methods
    private void validateManager(Long managerId) {
        if (managerId == null) {
            throw new UnauthenticatedException("managerId is required");
        }

        boolean isManager = userRepository.existsByIdAndRole(managerId, UserRole.manager);

        if (!isManager) {
            throw new ForbiddenException("Provided managerId is not a manager");
        }
    }

    public Long getNextCarId() {
        Long maxId = carRepository.findMaxId();
        return maxId + 1;
    }

}
