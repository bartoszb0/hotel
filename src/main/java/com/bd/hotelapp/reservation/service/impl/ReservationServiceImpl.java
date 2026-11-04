package com.bd.hotelapp.reservation.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.service.GuestService;
import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.reservation.exception.GuestCountExceedsCapacityException;
import com.bd.hotelapp.reservation.exception.InvalidStayDateException;
import com.bd.hotelapp.reservation.exception.RoomNotAvailableException;
import com.bd.hotelapp.reservation.repository.ReservationRepository;
import com.bd.hotelapp.reservation.service.ReservationService;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.service.RoomService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationServiceImpl implements ReservationService {

    private static final int MAX_NIGHTS = 30;

    private final RoomService roomService;
    private final ReservationRepository reservationRepository;
    private final Clock clock;
    private final GuestService guestService;


    public ReservationServiceImpl(RoomService roomService, ReservationRepository reservationRepository, Clock clock, GuestService guestService) {
        this.roomService = roomService;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
        this.guestService = guestService;
    }

    @Override
    @Transactional
    public Reservation createReservation(CreateReservationCommand createReservationCommand) {
        validateStayDate(createReservationCommand.checkIn(), createReservationCommand.checkOut());

        Room room = roomService.getRoom(createReservationCommand.roomId());

        validateCapacity(createReservationCommand.guestCount(), room);
        validateRoomAvailability(createReservationCommand.checkIn(), createReservationCommand.checkOut(), room);

        BigDecimal totalPrice = getTotalPrice(
                createReservationCommand.checkIn(),
                createReservationCommand.checkOut(),
                room.getRoomType().getPricePerNight()
        );

        Guest guest = guestService.findOrCreate(
                createReservationCommand.guestFirstName(),
                createReservationCommand.guestLastName(),
                createReservationCommand.guestEmail()
        );

        Reservation reservation = new Reservation(
                room,
                guest,
                createReservationCommand.checkIn(),
                createReservationCommand.checkOut(),
                createReservationCommand.guestCount(),
                totalPrice,
                Instant.now(clock)
        );

        return reservationRepository.save(reservation);
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
