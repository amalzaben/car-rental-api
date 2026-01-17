package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.Booking;
import com.rentgo.car_rental_service.model.ENUM.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
   SELECT b
   FROM Booking b
   WHERE b.customer.id = :customerId
""")
    Page<Booking> findByCustomerId(
            @Param("customerId") Long customerId,
            Pageable pageable
    );

    @Query("""
   SELECT b
   FROM Booking b
   WHERE b.customer.id = :customerId
     AND b.status = :status
""")
    Page<Booking> findByCustomerIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") BookingStatus status,
            Pageable pageable
    );

    @Query("""
   SELECT b
   FROM Booking b
   WHERE b.id = :bookingId
     AND b.customer.id = :customerId
""")
    Optional<Booking> findByIdAndCustomerId(
            @Param("bookingId") Long bookingId,
            @Param("customerId") Long customerId
    );

    @Query("""
   SELECT b
   FROM Booking b
   WHERE b.status = 'pending'
""")
    Page<Booking> findPendingBookings(Pageable pageable);

    @Query("""
   SELECT b
   FROM Booking b
   JOIN FETCH b.payment p
   WHERE b.id = :bookingId
""")
    Optional<Booking> findByIdWithPayment(@Param("bookingId") Long bookingId);

    @Query("""
   SELECT b
   FROM Booking b
   JOIN FETCH b.payment p
   JOIN FETCH b.car c
   WHERE b.id = :bookingId
""")
    Optional<Booking> findByIdWithPaymentAndCar(@Param("bookingId") Long bookingId);



}
