package com.bd.hotelapp.reservation.repository;

import com.bd.hotelapp.TestcontainersConfiguration;
import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ReservationRepositoryTest {

    private static final LocalDate EXISTING_CHECK_IN = LocalDate.of(2026, 3, 10);
    private static final LocalDate EXISTING_CHECK_OUT = LocalDate.of(2026, 3, 14);

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TestEntityManager entityManager;

    private RoomType roomType;
    private Room room;
    private Reservation existingReservation;

    @BeforeEach
    void setUp() {
        roomType = entityManager.persist(new RoomType("Double", 2, new BigDecimal("300.00")));
        room = entityManager.persist(new Room(101, roomType));
        Guest guest = entityManager.persist(new Guest("Jan", "Kowalski", "jan@example.com"));
        existingReservation = entityManager.persist(new Reservation(
                room, guest, EXISTING_CHECK_IN, EXISTING_CHECK_OUT, 2,
                new BigDecimal("1200.00"), Instant.parse("2026-01-10T10:00:00Z")));
        entityManager.flush();
    }

    @ParameterizedTest(name = "{0}: {1}..{2} -> {3}")
    @CsvSource({
            "A entirely before,                   2026-03-06, 2026-03-09, false",
            "B overlaps the start,                2026-03-08, 2026-03-11, true",
            "C inside,                            2026-03-11, 2026-03-13, true",
            "D overlaps the end,                  2026-03-12, 2026-03-16, true",
            "E check-in on existing check-out,    2026-03-14, 2026-03-17, false",
            "F fully covers existing,             2026-03-07, 2026-03-18, true",
            "G check-out on existing check-in,    2026-03-06, 2026-03-10, false",
            "H same dates as existing,            2026-03-10, 2026-03-14, true"
    })
    void shouldDetectOverlapOnSameRoom(String scenario, LocalDate checkIn, LocalDate checkOut, boolean expected) {
        boolean result = reservationRepository.existsOverlapping(room.getId(), checkIn, checkOut);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldNotDetectOverlapOnDifferentRoom() {
        Room otherRoom = entityManager.persistAndFlush(new Room(102, roomType));

        boolean result = reservationRepository.existsOverlapping(
                otherRoom.getId(), EXISTING_CHECK_IN, EXISTING_CHECK_OUT);

        assertThat(result).isFalse();
    }

    @Test
    void shouldIgnoreCancelledReservation() {
        existingReservation.cancel();
        entityManager.flush();
        entityManager.clear();

        boolean result = reservationRepository.existsOverlapping(
                room.getId(), EXISTING_CHECK_IN, EXISTING_CHECK_OUT);

        assertThat(result).isFalse();
    }
}
