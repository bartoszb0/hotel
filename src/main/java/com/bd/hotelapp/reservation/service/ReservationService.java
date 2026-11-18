package com.bd.hotelapp.reservation.service;

import com.bd.hotelapp.reservation.domain.command.CreateMyReservationCommand;
import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.entity.Reservation;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    Reservation createReservation(CreateReservationCommand createReservationCommand);

    Reservation getReservation(UUID id);

    List<Reservation> getMyReservations(UUID userId);

    List<Reservation> getAllReservations();

    Reservation createMyReservation(CreateMyReservationCommand createMyReservationCommand);

    Reservation cancelReservation(UUID id);
}
