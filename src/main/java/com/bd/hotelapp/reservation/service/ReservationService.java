package com.bd.hotelapp.reservation.service;

import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.entity.Reservation;

public interface ReservationService {

    Reservation createReservation(CreateReservationCommand createReservationCommand);
}
