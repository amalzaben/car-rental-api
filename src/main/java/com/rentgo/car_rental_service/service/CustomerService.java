package com.rentgo.car_rental_service.service;

import com.rentgo.car_rental_service.dto.controller.response.*;
import com.rentgo.car_rental_service.dto.service.CreateBookingCommand;
import com.rentgo.car_rental_service.exception.ConflictException;
import com.rentgo.car_rental_service.exception.ForbiddenException;
import com.rentgo.car_rental_service.exception.ResourceNotFoundException;
import com.rentgo.car_rental_service.exception.UnauthenticatedException;
import com.rentgo.car_rental_service.mapper.BookingMapper;
import com.rentgo.car_rental_service.mapper.CarMapper;
import com.rentgo.car_rental_service.model.*;
import com.rentgo.car_rental_service.model.ENUM.*;
import com.rentgo.car_rental_service.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final FavoriteCarRepository favoriteCarRepository;
    private final CarMapper carMapper;
    private final CarColorRepository carColorRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final DropOffRepository dropoffRepository;
    private final PickUpRepository pickupRepository;
    private final CustomerNotificationRepository customerNotificationRepository;
    private final ManagerNotificationRepository managerNotificationRepository;

    @Transactional
    public AddFavoriteCarResponse addCarToFavorites(Long userId, Long carId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + userId));

        // Optional but recommended: ensure role is customer
        if (user.getRole() != UserRole.customer) {
            throw new UnauthenticatedException("Only customers can favorite cars.");
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + carId));

        boolean alreadyFavorited = favoriteCarRepository.existsByUserIdAndCarId(userId, carId);
        if (alreadyFavorited) {
            throw new ConflictException("This Car Was Already Added to This User Favorites");
        }

        FavoriteCar favorite = FavoriteCar.builder()
                .user(user)
                .car(car)
                .build();
        try {
            favoriteCarRepository.save(favorite);
        } catch (DataIntegrityViolationException ex) {
            //it was inserted by another request just now
            throw new ConflictException("This Car Was Already Added to This User Favorites");
        }
        return new AddFavoriteCarResponse(userId, carId, true);
    }

    @Transactional
    public void removeFromFavorites(Long userId, Long carId) {

        // Optional but clean: validate user & car exist (gives nicer errors)
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!carRepository.existsById(carId)) {
            throw new ResourceNotFoundException("Car not found");
        }

        int deleted = favoriteCarRepository.deleteByUserIdAndCarId(userId, carId);
        if (deleted == 0) {
            throw new ResourceNotFoundException("Favorite car not found");
        }
    }

    @Transactional(readOnly = true)
    public Page<CarResponse> listFavoriteCars(Long userId, int page, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.customer) {
            throw new ForbiddenException("Only customers can have favorites");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Car> carsPage = favoriteCarRepository.findFavoriteCarsByUserId(userId, pageable);

        return carsPage.map(carMapper::toResponse);
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingCommand cmd) {

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

        if (!carColor.isAvailable()) {
            throw new ConflictException("This color is currently not available");
        }

        if (cmd.endDate().isBefore(cmd.startDate())) {
            throw new ConflictException("End date must be after start date");
        }

        validateCarColorAvailabilityOrDates(car, carColor, cmd.startDate(), cmd.endDate());

        long days = ChronoUnit.DAYS.between(cmd.startDate(), cmd.endDate()) + 1;
        BigDecimal totalPrice = car.getPrice().multiply(BigDecimal.valueOf(days));

        Payment payment = Payment.builder()
                .amount(totalPrice)
                .method(cmd.paymentMethod())
                .status(PaymentStatus.pending)
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
                .status(BookingStatus.pending)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        String carType = (booking.getCar() != null && booking.getCar().getType() != null)
                ? booking.getCar().getType()
                : "UNKNOWN_TYPE";

        String carName = (booking.getCar() != null && booking.getCar().getFamily() != null)
                ? booking.getCar().getFamily()
                : "Car";

        String msg = String.format(
                "New booking created In Store: %s (%s) booked from %s to %s.",
                carName,
                carType,
                booking.getStartDate(),
                booking.getEndDate()
        );

        ManagerNotification managerNotification = ManagerNotification.builder()
                .type(ManagerNotificationType.BOOKING_CREATED)
                .booking(booking)
                .message(msg)
                .isRead(false)
                .build();

        managerNotificationRepository.save(managerNotification);

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
    public Page<BookingResponse> listCustomerBookings(
            Long customerId,
            BookingStatus status,
            Pageable pageable
    ) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != UserRole.customer) {
            throw new ForbiddenException("User is not a customer");
        }

        Page<Booking> page = (status == null)
                ? bookingRepository.findByCustomerId(customerId, pageable)
                : bookingRepository.findByCustomerIdAndStatus(customerId, status, pageable);

        return page.map(b -> new BookingResponse(
                b.getId(),
                b.getCar().getId(),
                b.getStartDate(),
                b.getEndDate(),
                b.getTotalPrice(),
                b.getStatus(),
                b.getCreatedAt()
        ));
    }

    @Transactional(readOnly = true)
    public BookingDetailsResponse getBookingDetails(Long customerId, Long bookingId) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != UserRole.customer) {
            throw new ForbiddenException("User is not a customer");
        }

        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        return bookingMapper.toDetailsResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<CustomerNotificationResponse> listCustomerNotifications(Long customerId) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != UserRole.customer) {
            throw new ForbiddenException("User is not a customer");
        }

        List<CustomerNotification> notifications =
                customerNotificationRepository.findByCustomerId(customerId);

        return notifications.stream()
                .map(n -> new CustomerNotificationResponse(
                        n.getId(),
                        n.getBooking() != null ? n.getBooking().getId() : null,
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .toList();
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
