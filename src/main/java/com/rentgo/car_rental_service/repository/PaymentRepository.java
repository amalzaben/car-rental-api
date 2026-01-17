package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
