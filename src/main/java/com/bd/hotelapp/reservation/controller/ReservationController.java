package com.bd.hotelapp.reservation.controller;

import com.bd.hotelapp.common.web.ApiPaths;
import com.bd.hotelapp.reservation.domain.command.CreateReservationCommand;
import com.bd.hotelapp.reservation.domain.dto.CreateReservationRequest;
import com.bd.hotelapp.reservation.domain.dto.ReservationResponse;
import com.bd.hotelapp.reservation.domain.entity.Reservation;
import com.bd.hotelapp.reservation.mapper.ReservationMapper;
import com.bd.hotelapp.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.V1 + "/reservations")
public class ReservationController {

    private final ReservationMapper reservationMapper;
    private final ReservationService reservationService;

    public ReservationController(ReservationMapper reservationMapper, ReservationService reservationService) {
        this.reservationMapper = reservationMapper;
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid CreateReservationRequest createReservationRequest
    ) {
        CreateReservationCommand createReservationCommand = reservationMapper.toCommand(createReservationRequest);
        Reservation reservation = reservationService.createReservation(createReservationCommand);
        ReservationResponse reservationResponse = reservationMapper.toResponse(reservation);
        return new ResponseEntity<>(reservationResponse, HttpStatus.CREATED);
    }

}
