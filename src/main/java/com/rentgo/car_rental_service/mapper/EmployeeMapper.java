package com.rentgo.car_rental_service.mapper;

import com.rentgo.car_rental_service.dto.controller.request.UpdateEmployeeRequest;
import com.rentgo.car_rental_service.dto.controller.response.EmployeeResponse;
import com.rentgo.car_rental_service.dto.service.UpdateEmployeeCommand;
import com.rentgo.car_rental_service.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {



        @Mapping(target = "userId", source = "user.id")
        EmployeeResponse toResponse(Employee employee);


    UpdateEmployeeCommand toUpdateCommand(Long employeeId, UpdateEmployeeRequest request);
}
