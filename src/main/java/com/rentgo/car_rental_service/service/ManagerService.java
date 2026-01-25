package com.rentgo.car_rental_service.service;

import com.rentgo.car_rental_service.dto.controller.response.ManagerNotificationResponse;
import com.rentgo.car_rental_service.exception.ForbiddenException;
import com.rentgo.car_rental_service.exception.ResourceNotFoundException;
import com.rentgo.car_rental_service.model.ENUM.UserRole;
import com.rentgo.car_rental_service.model.Employee;
import com.rentgo.car_rental_service.model.ManagerNotification;
import com.rentgo.car_rental_service.model.User;
import com.rentgo.car_rental_service.repository.ManagerNotificationRepository;
import com.rentgo.car_rental_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository userRepository;
    private final ManagerNotificationRepository managerNotificationRepository;

    @Transactional(readOnly = true)
    public List<ManagerNotificationResponse> listManagerNotifications(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.manager) {
            throw new ForbiddenException("User is not a manager");
        }

        List<ManagerNotification> notifications =
                managerNotificationRepository.findAllOrderByCreatedAtDesc();

        return notifications.stream()
                .map(n -> new ManagerNotificationResponse(
                        n.getId(),
                        n.getType(),
                        n.getBooking() != null ? n.getBooking().getId() : null,
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .toList();
    }


}
