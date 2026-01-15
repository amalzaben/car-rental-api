package com.rentgo.car_rental_service.controller;

import com.rentgo.car_rental_service.dto.controller.request.CreateCarRequest;
import com.rentgo.car_rental_service.dto.controller.response.CarListItemResponse;
import com.rentgo.car_rental_service.dto.controller.response.CarResponse;
import com.rentgo.car_rental_service.dto.service.CreateCarCommand;
import com.rentgo.car_rental_service.mapper.CarMapper;
import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import com.rentgo.car_rental_service.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@RequestMapping("/cars")
@Controller
public class CarController {

    @Autowired
    private  CarService carService;
    @Autowired
    private  CarMapper carMapper;

    @PostMapping
    public ResponseEntity<CarResponse> addNewCar(@RequestBody @Valid CreateCarRequest request) {
        CreateCarCommand command = carMapper.toCommand(request);
        CarResponse response = carService.addNewCar(command);

        return ResponseEntity
                .created(URI.create("/cars/" + response.id()))
                .body(response);
    }

    @GetMapping("/{carId}")
    public ResponseEntity<CarResponse> getCarDetails(@PathVariable Long carId) {
        return ResponseEntity.ok(carService.getCarDetails(carId));
    }

    @GetMapping
    public ResponseEntity<Page<CarListItemResponse>> getCars(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) String fuel,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) CarStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));

        return ResponseEntity.ok(
                carService.getCars(q, type, color, seats, fuel, minPrice, maxPrice, status, pageable)
        );
    }

}
