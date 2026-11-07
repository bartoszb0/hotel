package com.bd.hotelapp.reservation.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.service.GuestService;
import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.reservation.domain.entity.ReservationStatus;
import com.bd.hotelapp.reservation.exception.*;
import com.bd.hotelapp.reservation.repository.ReservationRepository;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.service.RoomService;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 1, 10);
    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");
    private static final UUID ROOM_ID = UUID.randomUUID();
    private static final UUID RESERVATION_ID = UUID.randomUUID();

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomService roomService;
    @Mock
    private GuestService guestService;

    private Clock clock;
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(TODAY.atTime(10, 0).atZone(ZONE).toInstant(), ZONE);
        reservationService = new ReservationServiceImpl(roomService, reservationRepository, clock, guestService);
    }

    // =====================================================================
    // createReservation – happy path + granice
    // =====================================================================

    @Test
    void shouldCreateReservationWithCalculatedPrice() {
        Room room = doubleRoom();
        Guest guest = guest();
        LocalDate checkIn = TODAY.plusDays(5);
        LocalDate checkOut = TODAY.plusDays(8); // 3 noce
        stubSuccessfulCreation(room, guest, checkIn, checkOut);

        Reservation result = reservationService.createReservation(command(checkIn, checkOut, 2));

        assertThat(result.getTotalPrice()).isEqualByComparingTo("900.00");
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.getRoom()).isSameAs(room);
        assertThat(result.getGuest()).isSameAs(guest);
        assertThat(result.getCreatedAt()).isEqualTo(Instant.now(clock));
    }

    @Test
    void shouldAllowCheckInToday() {
        LocalDate checkIn = TODAY;
        LocalDate checkOut = TODAY.plusDays(1);
        stubSuccessfulCreation(doubleRoom(), guest(), checkIn, checkOut);

        Reservation result = reservationService.createReservation(command(checkIn, checkOut, 1));

        assertThat(result.getCheckIn()).isEqualTo(TODAY);
    }

    @Test
    void shouldAllowExactlyThirtyNights() {
        LocalDate checkIn = TODAY.plusDays(1);
        LocalDate checkOut = checkIn.plusDays(30);
        stubSuccessfulCreation(doubleRoom(), guest(), checkIn, checkOut);

        Reservation result = reservationService.createReservation(command(checkIn, checkOut, 1));

        assertThat(result.getTotalPrice()).isEqualByComparingTo("9000.00");
    }

    @Test
    void shouldAllowGuestCountEqualToCapacity() {
        LocalDate checkIn = TODAY.plusDays(1);
        LocalDate checkOut = TODAY.plusDays(2);
        stubSuccessfulCreation(doubleRoom(), guest(), checkIn, checkOut);

        Reservation result = reservationService.createReservation(command(checkIn, checkOut, 2));

        assertThat(result.getGuestCount()).isEqualTo(2);
    }

    // =====================================================================
    // createReservation – daty (walidacja PRZED pobraniem pokoju → nic nie stubujemy)
    // =====================================================================

    @Test
    void shouldRejectCheckInInThePast() {
        assertRejectedWithoutTouchingRoom(command(TODAY.minusDays(1), TODAY.plusDays(2), 1));
    }

    @Test
    void shouldRejectCheckOutEqualToCheckIn() {
        assertRejectedWithoutTouchingRoom(command(TODAY.plusDays(3), TODAY.plusDays(3), 1));
    }

    @Test
    void shouldRejectCheckOutBeforeCheckIn() {
        assertRejectedWithoutTouchingRoom(command(TODAY.plusDays(5), TODAY.plusDays(3), 1));
    }

    @Test
    void shouldRejectStayLongerThanThirtyNights() {
        LocalDate checkIn = TODAY.plusDays(1);
        assertRejectedWithoutTouchingRoom(command(checkIn, checkIn.plusDays(31), 1));
    }

    // createReservation – pojemność i dostępność

    @Test
    void shouldRejectTooManyGuests() {
        when(roomService.getRoom(ROOM_ID)).thenReturn(doubleRoom());

        assertThatThrownBy(() -> reservationService.createReservation(
                command(TODAY.plusDays(1), TODAY.plusDays(2), 3)))
                .isInstanceOf(GuestCountExceedsCapacityException.class);

        verifyNothingCreated();
    }

    @Test
    void shouldRejectWhenRoomIsNotAvailable() {
        LocalDate checkIn = TODAY.plusDays(1);
        LocalDate checkOut = TODAY.plusDays(4);
        when(roomService.getRoom(ROOM_ID)).thenReturn(doubleRoom());
        when(reservationRepository.existsOverlapping(ROOM_ID, checkIn, checkOut)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(command(checkIn, checkOut, 2)))
                .isInstanceOf(RoomNotAvailableException.class);

        verifyNothingCreated();
    }

    // cancelReservation

    @Test
    void shouldCancelReservationDayBeforeCheckIn() {
        Reservation reservation = reservation(TODAY.plusDays(1), TODAY.plusDays(3));
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.cancelReservation(RESERVATION_ID);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void shouldRejectCancellationOnCheckInDay() {
        Reservation reservation = reservation(TODAY, TODAY.plusDays(2));
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(TooLateForReservationCancelException.class);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectCancellationAfterCheckIn() {
        Reservation reservation = reservation(TODAY.minusDays(2), TODAY.plusDays(1));
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(TooLateForReservationCancelException.class);
    }

    @Test
    void shouldRejectCancellingAlreadyCancelledReservation() {
        Reservation reservation = reservation(TODAY.plusDays(5), TODAY.plusDays(7));
        reservation.cancel();
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(ReservationAlreadyCancelledException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCancellingNonExistingReservation() {
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    // helpers

    private Room doubleRoom() {
        RoomType type = new RoomType("Double", 2, new BigDecimal("300.00"));
        Room room = new Room(101, type);
        ReflectionTestUtils.setField(room, "id", ROOM_ID);
        return room;
    }

    private Guest guest() {
        return new Guest("Jan", "Kowalski", "jan@example.com");
    }

    private Reservation reservation(LocalDate checkIn, LocalDate checkOut) {
        Reservation reservation = new Reservation(
                doubleRoom(), guest(), checkIn, checkOut, 2, new BigDecimal("600.00"), Instant.now(clock));
        ReflectionTestUtils.setField(reservation, "id", RESERVATION_ID);
        return reservation;
    }

    private CreateReservationCommand command(LocalDate checkIn, LocalDate checkOut, int guestCount) {
        return new CreateReservationCommand(
                ROOM_ID, "Jan", "Kowalski", "jan@example.com", checkIn, checkOut, guestCount);
    }

    private void stubSuccessfulCreation(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        when(roomService.getRoom(ROOM_ID)).thenReturn(room);
        when(reservationRepository.existsOverlapping(ROOM_ID, checkIn, checkOut)).thenReturn(false);
        when(guestService.findOrCreate("Jan", "Kowalski", "jan@example.com")).thenReturn(guest);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void assertRejectedWithoutTouchingRoom(CreateReservationCommand command) {
        assertThatThrownBy(() -> reservationService.createReservation(command))
                .isInstanceOf(InvalidStayDateException.class);

        verify(roomService, never()).getRoom(any());
        verifyNothingCreated();
    }

    private void verifyNothingCreated() {
        verify(guestService, never()).findOrCreate(any(), any(), any());
        verify(reservationRepository, never()).save(any());
    }
}