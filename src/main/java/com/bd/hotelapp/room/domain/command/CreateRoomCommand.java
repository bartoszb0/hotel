package com.bd.hotelapp.room.domain.command;

import java.util.UUID;

public record CreateRoomCommand(
        Integer number,
        UUID roomTypeId
) {
}
