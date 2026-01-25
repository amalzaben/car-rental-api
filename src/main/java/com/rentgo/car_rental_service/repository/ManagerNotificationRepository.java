package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.CustomerNotification;
import com.rentgo.car_rental_service.model.ManagerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ManagerNotificationRepository extends JpaRepository<ManagerNotification, Long> {

    @Query("""
        SELECT mn
        FROM ManagerNotification mn
        ORDER BY mn.createdAt DESC
    """)
    List<ManagerNotification> findAllOrderByCreatedAtDesc();
}
