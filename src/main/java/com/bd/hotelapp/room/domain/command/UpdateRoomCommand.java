package com.bd.hotelapp.room.domain.command;

import java.util.UUID;

public record UpdateRoomCommand(
        Integer number,
        UUID roomTypeId
) {
}
