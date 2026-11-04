package com.bd.hotelapp.reservation.mapper;

import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.dto.CreateReservationRequest;
import com.bd.hotelapp.reservation.domain.dto.ReservationResponse;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    CreateReservationCommand toCommand(CreateReservationRequest createReservationRequest);

    ReservationResponse toResponse(Reservation reservation);

}
