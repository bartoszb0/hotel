package com.bd.hotelapp.availability.specification;

import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.reservation.domain.entity.ReservationStatus;
import com.bd.hotelapp.room.domain.entity.Room;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RoomSearchSpecifications {

    private RoomSearchSpecifications() {
    }

    public static Specification<Room> hasRoomType(UUID roomTypeId) {
        return (root, query, cb) -> cb.equal(root.get("roomType").get("id"), roomTypeId);
    }

    public static Specification<Room> hasMinCapacity(Integer minCapacity) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("roomType").<Integer>get("capacity"), minCapacity);
    }

    public static Specification<Room> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("roomType").<BigDecimal>get("pricePerNight"), maxPrice);
    }

    public static Specification<Room> isAvailableBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            Subquery<UUID> overlapping = query.subquery(UUID.class);
            Root<Reservation> reservation = overlapping.from(Reservation.class);

            overlapping.select(reservation.<UUID>get("id"))
                    .where(
                            cb.equal(reservation.get("room"), root),
                            cb.equal(reservation.get("status"), ReservationStatus.CONFIRMED),
                            cb.lessThan(reservation.<LocalDate>get("checkIn"), to),
                            cb.greaterThan(reservation.<LocalDate>get("checkOut"), from)
                    );

            return cb.not(cb.exists(overlapping));
        };
    }
}
