package com.bd.hotelapp.availability.specification;

import com.bd.hotelapp.TestcontainersConfiguration;
import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.repository.RoomRepository;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class RoomSearchSpecificationsTest {

    private static final LocalDate EXISTING_CHECK_IN = LocalDate.of(2026, 3, 10);
    private static final LocalDate EXISTING_CHECK_OUT = LocalDate.of(2026, 3, 14);

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private TestEntityManager entityManager;

    private RoomType doubleType;

    @BeforeEach
    void setUp() {
        doubleType = entityManager.persist(new RoomType("Double", 2, new BigDecimal("300.00")));
        RoomType singleType = entityManager.persist(new RoomType("Single", 1, new BigDecimal("200.00")));
        RoomType suiteType = entityManager.persist(new RoomType("Suite", 5, new BigDecimal("850.00")));

        Room room101 = entityManager.persist(new Room(101, doubleType));
        entityManager.persist(new Room(102, singleType));
        entityManager.persist(new Room(103, suiteType));
        Room room104 = entityManager.persist(new Room(104, doubleType));

        Guest guest = entityManager.persist(new Guest("Jan", "Kowalski", "jan@example.com"));

        entityManager.persist(new Reservation(
                room101, guest, EXISTING_CHECK_IN, EXISTING_CHECK_OUT, 2,
                new BigDecimal("1200.00"), Instant.parse("2026-01-10T10:00:00Z")));

        Reservation cancelled = entityManager.persist(new Reservation(
                room104, guest, EXISTING_CHECK_IN, EXISTING_CHECK_OUT, 2,
                new BigDecimal("1200.00"), Instant.parse("2026-01-10T10:00:00Z")));
        cancelled.cancel();

        entityManager.flush();
    }

    @Test
    void shouldReturnAllRoomsWhenNoSpecifications() {
        List<Room> result = roomRepository.findAll(Specification.allOf(List.of()));

        assertThat(result).extracting(Room::getNumber).containsExactlyInAnyOrder(101, 102, 103, 104);
    }

    @Test
    void shouldReturnRoomsWithCapacityAtLeastMinCapacity() {
        List<Room> result = roomRepository.findAll(RoomSearchSpecifications.hasMinCapacity(2));

        assertThat(result).extracting(Room::getNumber).containsExactlyInAnyOrder(101, 103, 104);
    }

    @Test
    void shouldReturnRoomsWithPriceUpToAndIncludingMaxPrice() {
        List<Room> result = roomRepository.findAll(RoomSearchSpecifications.hasMaxPrice(new BigDecimal("300.00")));

        assertThat(result).extracting(Room::getNumber).containsExactlyInAnyOrder(101, 102, 104);
    }

    @Test
    void shouldReturnRoomsOfGivenRoomType() {
        List<Room> result = roomRepository.findAll(RoomSearchSpecifications.hasRoomType(doubleType.getId()));

        assertThat(result).extracting(Room::getNumber).containsExactlyInAnyOrder(101, 104);
    }

    static Stream<Arguments> availabilityScenarios() {
        return Stream.of(
                arguments("same dates as existing (104 only CANCELLED)",
                        LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 14), List.of(102, 103, 104)),
                arguments("check-in on existing check-out",
                        LocalDate.of(2026, 3, 14), LocalDate.of(2026, 3, 16), List.of(101, 102, 103, 104)),
                arguments("check-out on existing check-in",
                        LocalDate.of(2026, 3, 6), LocalDate.of(2026, 3, 10), List.of(101, 102, 103, 104)),
                arguments("overlaps the end",
                        LocalDate.of(2026, 3, 13), LocalDate.of(2026, 3, 15), List.of(102, 103, 104))
        );
    }

    @ParameterizedTest(name = "{0}: {1}..{2} -> {3}")
    @MethodSource("availabilityScenarios")
    void shouldReturnRoomsWithoutOverlappingConfirmedReservation(
            String scenario, LocalDate from, LocalDate to, List<Integer> expectedRoomNumbers) {
        List<Room> result = roomRepository.findAll(RoomSearchSpecifications.isAvailableBetween(from, to));

        assertThat(result).extracting(Room::getNumber).containsExactlyInAnyOrderElementsOf(expectedRoomNumbers);
    }

    @Test
    void shouldApplyAllCombinedSpecifications() {
        Specification<Room> spec = Specification.allOf(
                RoomSearchSpecifications.hasMinCapacity(2),
                RoomSearchSpecifications.hasMaxPrice(new BigDecimal("400.00")),
                RoomSearchSpecifications.isAvailableBetween(LocalDate.of(2026, 3, 11), LocalDate.of(2026, 3, 12)));

        List<Room> result = roomRepository.findAll(spec);

        assertThat(result).extracting(Room::getNumber).containsExactly(104);
    }
}
