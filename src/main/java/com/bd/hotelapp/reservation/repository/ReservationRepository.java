package com.bd.hotelapp.reservation.repository;

import com.bd.hotelapp.reservation.domain.entity.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.room.id = :roomId
              AND r.status = com.bd.hotelapp.reservation.domain.entity.ReservationStatus.CONFIRMED
              AND r.checkIn < :checkOut
              AND r.checkOut > :checkIn
            """)
    boolean existsOverlapping(@Param("roomId") UUID roomId,
                              @Param("checkIn") LocalDate checkIn,
                              @Param("checkOut") LocalDate checkOut);

    @Override
    @EntityGraph(attributePaths = {"room", "guest", "room.roomType"})
    Optional<Reservation> findById(UUID id);

    @EntityGraph(attributePaths = {"room", "guest", "room.roomType"})
    List<Reservation> findByGuestEmail(String guestEmail);
}
