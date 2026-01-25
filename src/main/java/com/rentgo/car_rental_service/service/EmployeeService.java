package com.rentgo.car_rental_service.service;

import com.rentgo.car_rental_service.dto.controller.response.*;
import com.rentgo.car_rental_service.dto.service.ManualCreateBookingCommand;
import com.rentgo.car_rental_service.dto.service.UpdateEmployeeCommand;
import com.rentgo.car_rental_service.exception.ConflictException;
import com.rentgo.car_rental_service.exception.ForbiddenException;
import com.rentgo.car_rental_service.exception.ResourceNotFoundException;
import com.rentgo.car_rental_service.mapper.BookingMapper;
import com.rentgo.car_rental_service.mapper.EmployeeMapper;
import com.rentgo.car_rental_service.model.*;
import com.rentgo.car_rental_service.model.ENUM.*;
import com.rentgo.car_rental_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EmployeeRepository employeeRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerNotificationRepository customerNotificationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final CarColorRepository carColorRepository;
    private final PickUpRepository pickupRepository;
    private final DropOffRepository dropoffRepository;
    private final PickUpRepository pickUpRepository;
    private final DropOffRepository dropOffRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public Page<PendingBookingResponse> listPendingBookings(Long employeeId, Pageable pageable) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getUser() == null || employee.getUser().getRole() != UserRole.employee) {
            throw new ForbiddenException("User is not an employee");
        }
        if (employee.getRole() != EmployeeRole.online_booking_officer)
            throw new ForbiddenException("This Employee is not an online booking officer");

        return bookingRepository.findPendingBookings(pageable)
                .map(bookingMapper::toPendingBookingResponse);
    }

    @Transactional
    public RejectBookingResponse rejectBooking(Long employeeId, Long bookingId, String reason) {

        // employeeId should be employees.id (not users.id)
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getUser() == null || employee.getUser().getRole() != UserRole.employee) {
            throw new ForbiddenException("User is not an employee");
        }

        Booking booking = bookingRepository.findByIdWithPayment(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Only pending can be rejected
        if (booking.getStatus() != BookingStatus.pending) {
            throw new ConflictException("Only pending bookings can be rejected");
        }

        // Assign who handled it
        booking.setHandledByEmployee(employee);

        booking.setStatus(BookingStatus.rejected);

        Payment payment = booking.getPayment();
        if (payment != null) {
            if (payment.getStatus() == PaymentStatus.pending) {
                payment.setStatus(PaymentStatus.failed);
            } else if (payment.getStatus() == PaymentStatus.paid) {
                payment.setStatus(PaymentStatus.refunded);
            }
            paymentRepository.save(payment);
        }

        bookingRepository.save(booking);

        // Create customer notification
        String msg = (reason == null || reason.isBlank())
                ? "Your booking request was rejected."
                : "Your booking request was rejected. Reason: " + reason;

        CustomerNotification notification = CustomerNotification.builder()
                .booking(booking)
                .message(msg)
                .type(CustomerNotificationType.booking_rejected)
                .isRead(false)
                .build();

        customerNotificationRepository.save(notification);

        return new RejectBookingResponse(
                booking.getId(),
                booking.getStatus(),
                payment != null ? payment.getStatus() : null,
                LocalDateTime.now()
        );
    }

    @Transactional
    public ApproveBookingResponse approveBooking(Long employeeId, Long bookingId, String note) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getUser() == null || employee.getUser().getRole() != UserRole.employee) {
            throw new ForbiddenException("User is not an employee");
        }

        Booking booking = bookingRepository.findByIdWithPaymentAndCar(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.pending) {
            throw new ConflictException("Only pending bookings can be approved");
        }

        if (booking.getCar().getStatus() == CarStatus.in_maintenance) {
            throw new ConflictException("Car is under maintenance");
        }

        booking.setHandledByEmployee(employee);

        booking.setStatus(BookingStatus.approved);

        Payment payment = booking.getPayment();
        if (payment == null) {
            throw new ConflictException("Booking has no payment record");
        }

        if (payment.getStatus() != PaymentStatus.pending) {
            throw new ConflictException("Payment status must be pending to approve booking");
        }
        payment.setStatus(PaymentStatus.paid);

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        // update car status
        // booking.getCar().setStatus(CarStatus.booked);
        // carRepository.save(booking.getCar());

        String msg = "Your booking request was approved.";
        if (note != null && !note.isBlank()) {
            msg += " Note: " + note;
        }

        CustomerNotification notification = CustomerNotification.builder()
                .booking(booking)
                .message(msg)
                .type(CustomerNotificationType.booking_confirmed)
                .isRead(false)
                .build();

        customerNotificationRepository.save(notification);

        return new ApproveBookingResponse(
                booking.getId(),
                booking.getStatus(),
                payment.getStatus(),
                LocalDateTime.now()
        );
    }

    @Transactional
    public BookingResponse manuallyCreateBooking(ManualCreateBookingCommand cmd) {

        Employee employee = employeeRepository.findById(cmd.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != EmployeeRole.customer_service) {
            throw new ForbiddenException("Employee is not customer service");
        }

        User customer = userRepository.findById(cmd.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != UserRole.customer) {
            throw new ForbiddenException("User is not a customer");
        }

        Car car = carRepository.findById(cmd.carId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        if (car.getStatus() == CarStatus.in_maintenance) {
            throw new ConflictException("Car is under maintenance");
        }

        CarColor carColor = carColorRepository
                .findByCarIdAndColorName(cmd.carId(), cmd.carColor())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Color '" + cmd.carColor() + "' not available for this car"
                ));

        validateCarColorAvailabilityOrDates(car, carColor, cmd.startDate(), cmd.endDate());

        if (cmd.endDate().isBefore(cmd.startDate())) {
            throw new ConflictException("End date must be after start date");
        }

        long days = ChronoUnit.DAYS.between(cmd.startDate(), cmd.endDate()) + 1;
        BigDecimal totalPrice = car.getPrice().multiply(BigDecimal.valueOf(days));

        Payment payment = Payment.builder()
                .amount(totalPrice)
                .method(cmd.paymentMethod())
                .status(PaymentStatus.paid)
                .build();
        paymentRepository.save(payment);

        Booking booking = Booking.builder()
                .customer(customer)
                .car(car)
                .carColor(carColor)
                .startDate(cmd.startDate())
                .endDate(cmd.endDate())
                .location(cmd.location())
                .phoneNumber(cmd.phoneNumber())
                .personalId(cmd.personalIdImage())
                .drivingLicense(cmd.drivingLicense())
                .totalPrice(totalPrice)
                .payment(payment)

                .status(BookingStatus.approved)
                .createdAt(LocalDateTime.now())

                .handledByEmployee(employee)

                .build();

        bookingRepository.save(booking);

        DropOff dropOff = new DropOff(booking, cmd.startDate().atStartOfDay());
        dropoffRepository.save(dropOff);


        PickUp pickUp = new PickUp(booking, cmd.endDate().atStartOfDay());
        pickupRepository.save(pickUp);
        carColor.setAvailable(false);
        carColorRepository.save(carColor);

        return new BookingResponse(
                booking.getId(),
                booking.getCar().getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<DueDeliveryResponse> getTodayUnassignedDeliveries(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != EmployeeRole.delivery) {
            throw new ForbiddenException("Employee is not a delivery employee");
        }

        LocalDate today = LocalDate.now(); // optionally: LocalDate.now(ZoneId.of("Asia/Hebron"))
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<PickUp> pickups = pickUpRepository
                .findTodayUnassignedPickups(startOfDay, endOfDay);

        List<DropOff> dropoffs = dropOffRepository
                .findTodayUnassignedDropoffs(startOfDay, endOfDay);

        List<DueDeliveryResponse> result = new ArrayList<>(pickups.size() + dropoffs.size());

        for (PickUp p : pickups) {
            Booking b = p.getBooking();
            Car c = b.getCar();

            result.add(new DueDeliveryResponse(
                    p.getId(),
                    b.getId(),
                    DeliveryType.PICKUP,
                    p.getDate(),
                    b.getLocation(),
                    b.getPhoneNumber(),
                    c.getPicture(),
                    c.getType(),
                    c.getFamily()
            ));
        }


        for (DropOff d : dropoffs) {
            Booking b = d.getBooking();
            Car c = b.getCar();

            result.add(new DueDeliveryResponse(
                    d.getId(),
                    b.getId(),
                    DeliveryType.DROPOFF,
                    d.getDate(),
                    b.getLocation(),
                    b.getPhoneNumber(),
                    c.getPicture(),
                    c.getType(),
                    c.getFamily()
            ));
        }

        // Optional: sort by due time
        result.sort(Comparator.comparing(DueDeliveryResponse::dueDate));

        return result;
    }




    @Transactional
    public DueDeliveryResponse assignPickup(Long employeeId, Long pickupId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != EmployeeRole.delivery) {
            throw new ForbiddenException("Employee is not a delivery employee");
        }

        PickUp pickup = pickUpRepository.findByIdForUpdate(pickupId)
                .orElseThrow(() -> new ResourceNotFoundException("PickUp not found"));

        if (pickup.isAssigned()) {
            throw new ConflictException("PickUp already assigned");
        }

        pickup.assignToEmployee(employeeId);

        Booking b = pickup.getBooking();
        Car c = b.getCar();

        return new DueDeliveryResponse(
                pickup.getId(),
                b.getId(),
                DeliveryType.PICKUP,
                pickup.getDate(),
                b.getLocation(),
                b.getPhoneNumber(),
                c.getPicture(),
                c.getType(),
                c.getFamily()
        );
    }

    @Transactional
    public DueDeliveryResponse assignDropoff(Long employeeId, Long dropoffId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != EmployeeRole.delivery) {
            throw new ForbiddenException("Employee is not a delivery employee");
        }

        DropOff dropoff = dropOffRepository.findByIdForUpdate(dropoffId)
                .orElseThrow(() -> new ResourceNotFoundException("DropOff not found"));

        if (dropoff.isAssigned()) {
            throw new ConflictException("DropOff already assigned");
        }

        dropoff.assignToEmployee(employeeId);

        Booking b = dropoff.getBooking();
        Car c = b.getCar();

        return new DueDeliveryResponse(
                dropoff.getId(),
                b.getId(),
                DeliveryType.DROPOFF,
                dropoff.getDate(),
                b.getLocation(),
                b.getPhoneNumber(),
                c.getPicture(),
                c.getType(),
                c.getFamily()
        );
    }

    @Transactional(readOnly = true)
    public List<DueDeliveryResponse> getAssignedDeliveries(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() != EmployeeRole.delivery) {
            throw new ForbiddenException("Employee is not a delivery employee");
        }

        List<PickUp> pickups = pickUpRepository.findAssignedToEmployee(employeeId);
        List<DropOff> dropoffs = dropOffRepository.findAssignedToEmployee(employeeId);

        List<DueDeliveryResponse> result = new ArrayList<>(pickups.size() + dropoffs.size());

        for (PickUp p : pickups) {
            Booking b = p.getBooking();
            Car c = b.getCar();
            result.add(new DueDeliveryResponse(
                    p.getId(),
                    b.getId(),
                    DeliveryType.PICKUP,
                    p.getDate(),
                    b.getLocation(),
                    b.getPhoneNumber(),
                    c.getPicture(),
                    c.getType(),
                    c.getFamily()
            ));
        }

        for (DropOff d : dropoffs) {
            Booking b = d.getBooking();
            Car c = b.getCar();
            result.add(new DueDeliveryResponse(
                    d.getId(),
                    b.getId(),
                    DeliveryType.DROPOFF,
                    d.getDate(),
                    b.getLocation(),
                    b.getPhoneNumber(),
                    c.getPicture(),
                    c.getType(),
                    c.getFamily()
            ));
        }

        result.sort(Comparator.comparing(DueDeliveryResponse::dueDate));
        return result;
    }

    @Transactional
    public EmployeeResponse updateEmployeeInfo(UpdateEmployeeCommand cmd) {

        Employee employee = employeeRepository.findById(cmd.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (cmd.getIdNumber() != null) {
            employee.setIdNumber(cmd.getIdNumber().trim());
        }

        if (cmd.getName() != null) {
            employee.setName(cmd.getName().trim());
        }

        if (cmd.getEmail() != null) {
            String normalizedEmail = cmd.getEmail().trim().toLowerCase();

            if (Boolean.TRUE.equals(
                    employeeRepository.existsByEmailIgnoreCaseAndIdNot(cmd.getEmail(), cmd.getEmployeeId())
            )) {
                throw new ConflictException("Email already in use");
            }


            employee.setEmail(normalizedEmail);
        }

        if (cmd.getDob() != null) {
            employee.setDob(cmd.getDob());
        }

        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }


    //helper method
    private void validateCarColorAvailabilityOrDates(Car car, CarColor carColor,
                                                     LocalDate startDate, LocalDate endDate) {
        if (carColor.isAvailable()) {
            return;
        }

        boolean hasOverlap = bookingRepository.existsOverlappingBooking(
                car.getId(),
                carColor.getId(),
                List.of(BookingStatus.approved, BookingStatus.pending),
                startDate,
                endDate
        );

        if (hasOverlap) {
            throw new ConflictException("Car is already booked for the selected dates");
        }

    }

}
