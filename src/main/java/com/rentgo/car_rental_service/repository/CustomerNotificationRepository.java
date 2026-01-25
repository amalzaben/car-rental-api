package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.CustomerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Long> {

        @Query("""
        SELECT cn
        FROM CustomerNotification cn
        JOIN cn.booking b
        JOIN b.customer c
        WHERE c.id = :customerId
        ORDER BY cn.createdAt DESC
    """)
        List<CustomerNotification> findByCustomerId(
                @Param("customerId") Long customerId
        );


}
