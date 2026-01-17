package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.CustomerNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Long> {
}
