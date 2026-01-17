package com.rentgo.car_rental_service.mapper;

import com.rentgo.car_rental_service.dto.service.AddFavoriteCarCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoriteCarMapper {
    AddFavoriteCarCommand toCommand(Long userId, Long carId);
}
