package com.bd.hotelapp.reservation.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.service.GuestService;
import com.bd.hotelapp.reservation.domain.command.CreateMyReservationCommand;
import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.reservation.domain.entity.ReservationStatus;
import com.bd.hotelapp.reservation.exception.*;
import com.bd.hotelapp.reservation.repository.ReservationRepository;
import com.bd.hotelapp.reservation.service.ReservationService;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.service.RoomService;
import com.bd.hotelapp.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final int MAX_NIGHTS = 30;

    private final RoomService roomService;
    private final ReservationRepository reservationRepository;
    private final Clock clock;
    private final GuestService guestService;
    private final UserService userService;

    public ReservationServiceImpl(RoomService roomService, ReservationRepository reservationRepository, Clock clock, GuestService guestService, UserService userService) {
        this.roomService = roomService;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
        this.guestService = guestService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Reservation createReservation(CreateReservationCommand createReservationCommand) {
        return placeReservation(
                () -> guestService.findOrCreate(
                        createReservationCommand.guestFirstName(),
                        createReservationCommand.guestLastName(),
                        createReservationCommand.guestEmail()
                ),
                createReservationCommand.roomId(),
                createReservationCommand.checkIn(),
                createReservationCommand.checkOut(),
                createReservationCommand.guestCount()
        );
    }

    @Override
    public Reservation getReservation(UUID id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Override
    public List<Reservation> getMyReservations(UUID userId) {
        UUID guestId = userService.getGuestId(userId);
        return reservationRepository.findByGuestId(guestId);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public Reservation createMyReservation(CreateMyReservationCommand createMyReservationCommand) {
        return placeReservation(
                () -> guestService.getGuest(userService.getGuestId(createMyReservationCommand.userId())),
                createMyReservationCommand.roomId(),
                createMyReservationCommand.checkIn(),
                createMyReservationCommand.checkOut(),
                createMyReservationCommand.guestCount()
        );
    }

    private Reservation placeReservation(Supplier<Guest> guestResolver, UUID roomId, LocalDate checkIn, LocalDate checkOut, Integer guestCount) {
        validateStayDate(checkIn, checkOut);

        Room room = roomService.getRoom(roomId);

        validateCapacity(guestCount, room);
        validateRoomAvailability(checkIn, checkOut, room);

        BigDecimal totalPrice = getTotalPrice(checkIn, checkOut, room.getRoomType().getPricePerNight());

        // resolved only after all validations pass, so a rejected request never creates a guest
        Guest guest = guestResolver.get();

        Reservation reservation = new Reservation(
                room,
                guest,
                checkIn,
                checkOut,
                guestCount,
                totalPrice,
                Instant.now(clock)
        );

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation cancelReservation(UUID id) {
        Reservation reservation = getReservation(id);

        validateCancellability(reservation);

        reservation.cancel();

        return reservationRepository.save(reservation);
    }

    private void validateCancellability(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationAlreadyCancelledException(reservation.getId());
        }

        if (!LocalDate.now(clock).isBefore(reservation.getCheckIn())) {
            throw new TooLateForReservationCancelException(reservation.getId());
        }
    }

    private void validateCapacity(Integer guestCount, Room room) {
        int capacity = room.getRoomType().getCapacity();

        if (guestCount > capacity) {
            throw new GuestCountExceedsCapacityException(guestCount, capacity);
        }
    }

    private void validateStayDate(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now(clock);

        if (checkIn.isBefore(today)) {
            throw new InvalidStayDateException("Check in date cannot be before today");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidStayDateException("Check out date must be after check in date");
        }

        if (getStayNightsCount(checkIn, checkOut) > MAX_NIGHTS) {
            throw new InvalidStayDateException("Stay length cannot be longer than %s nights".formatted(MAX_NIGHTS));
        }
    }

    private long getStayNightsCount(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    private BigDecimal getTotalPrice(LocalDate checkIn, LocalDate checkOut, BigDecimal pricePerNight) {
        long nights = getStayNightsCount(checkIn, checkOut);
        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }

    private void validateRoomAvailability(LocalDate checkIn, LocalDate checkOut, Room room) {
        if (reservationRepository.existsOverlapping(room.getId(), checkIn, checkOut)) {
            throw new RoomNotAvailableException("Room %s is not available in %s - %s".formatted(room.getNumber(), checkIn, checkOut));
        }
    }
}
