package com.rentgo.car_rental_service.service;

import com.rentgo.car_rental_service.dto.BookingReport;
import com.rentgo.car_rental_service.dto.CarsReport;
import com.rentgo.car_rental_service.dto.ProfitReport;
import com.rentgo.car_rental_service.dto.controller.response.ManagerNotificationResponse;
import com.rentgo.car_rental_service.dto.controller.response.MonthlyReportResponse;
import com.rentgo.car_rental_service.exception.ForbiddenException;
import com.rentgo.car_rental_service.exception.ResourceNotFoundException;
import com.rentgo.car_rental_service.model.ENUM.BookingStatus;
import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import com.rentgo.car_rental_service.model.ENUM.PaymentStatus;
import com.rentgo.car_rental_service.model.ENUM.UserRole;
import com.rentgo.car_rental_service.model.Employee;
import com.rentgo.car_rental_service.model.ManagerNotification;
import com.rentgo.car_rental_service.model.User;
import com.rentgo.car_rental_service.repository.BookingRepository;
import com.rentgo.car_rental_service.repository.CarRepository;
import com.rentgo.car_rental_service.repository.ManagerNotificationRepository;
import com.rentgo.car_rental_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository userRepository;
    private final ManagerNotificationRepository managerNotificationRepository;
    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;

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

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(int year, int month) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // 1) Cars status counts (pie)
        long bookedCars = carRepository.countByStatus(CarStatus.booked);
        long inMaintenanceCars = carRepository.countByStatus(CarStatus.in_maintenance);
        long availableCars = carRepository.countByStatus(CarStatus.available);

        // 2) Booking counts for the selected month (startDate within month)
        long accepted = bookingRepository.countByStatusAndStartDateBetween(
                BookingStatus.approved, start, end
        );

        long canceled = bookingRepository.countByStatusAndStartDateBetween(
                BookingStatus.canceled, start, end
        );

        long requested = bookingRepository.countByStatusAndStartDateBetween(
                BookingStatus.pending, start, end
        );

        // 3) Profit + rented cars (paid bookings in that month)
        BigDecimal totalProfit = bookingRepository.sumTotalPriceForPaidBookingsByStartDateBetween(
                PaymentStatus.paid, start, end
        );
        if (totalProfit == null) totalProfit = BigDecimal.ZERO;

        long rentedCars = bookingRepository.countPaidBookingsByStartDateBetween(
                PaymentStatus.paid, start, end
        );

        return new MonthlyReportResponse(
                year,
                month,
                new CarsReport(bookedCars, inMaintenanceCars, availableCars),
                new ProfitReport(totalProfit, rentedCars),
                new BookingReport(accepted, canceled, requested)
        );
    }



}
