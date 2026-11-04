package com.bd.hotelapp.reservation.mapper;

import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.dto.CreateReservationRequest;
import com.bd.hotelapp.reservation.domain.dto.ReservationResponse;
import com.bd.hotelapp.reservation.domain.entity.Reservation;

public interface ReservationMapper {

    CreateReservationCommand toCommand(CreateReservationRequest createReservationRequest);

    ReservationResponse toResponse(Reservation reservation);

}
