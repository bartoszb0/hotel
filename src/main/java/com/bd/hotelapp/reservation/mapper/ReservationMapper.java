package com.bd.hotelapp.reservation.mapper;

import com.bd.hotelapp.reservation.domain.command.CreateMyReservationCommand;
import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.dto.CreateMyReservationRequest;
import com.bd.hotelapp.reservation.domain.dto.CreateReservationRequest;
import com.bd.hotelapp.reservation.domain.dto.ReservationResponse;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    CreateReservationCommand toCommand(CreateReservationRequest createReservationRequest);

    ReservationResponse toResponse(Reservation reservation);

    CreateMyReservationCommand toCommand(CreateMyReservationRequest createMyReservationRequest, UUID userId);

    List<ReservationResponse> toResponse(List<Reservation> reservations);

}
