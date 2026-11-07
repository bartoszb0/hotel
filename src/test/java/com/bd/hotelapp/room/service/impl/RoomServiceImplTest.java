package com.bd.hotelapp.room.service.impl;

import com.bd.hotelapp.room.domain.command.CreateRoomCommand;
import com.bd.hotelapp.room.domain.command.UpdateRoomCommand;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.exception.RoomAlreadyExistsException;
import com.bd.hotelapp.room.exception.RoomNotFoundException;
import com.bd.hotelapp.room.mapper.RoomMapper;
import com.bd.hotelapp.room.repository.RoomRepository;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.exception.RoomTypeNotFoundException;
import com.bd.hotelapp.roomtype.service.RoomTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    private static final UUID ROOM_ID = UUID.randomUUID();
    private static final UUID ROOM_TYPE_ID = UUID.randomUUID();

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomTypeService roomTypeService;
    @Mock
    private RoomMapper roomMapper;

    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomServiceImpl(roomRepository, roomMapper, roomTypeService);
    }

    // createRoom

    @Test
    void shouldRejectCreatingRoomWithDuplicateNumber() {
        when(roomRepository.existsByNumber(101)).thenReturn(true);

        assertThatThrownBy(() -> roomService.createRoom(new CreateRoomCommand(101, ROOM_TYPE_ID)))
                .isInstanceOf(RoomAlreadyExistsException.class);

        verify(roomTypeService, never()).getRoomType(any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldPropagateExceptionWhenCreatingRoomWithUnknownRoomType() {
        when(roomRepository.existsByNumber(101)).thenReturn(false);
        when(roomTypeService.getRoomType(ROOM_TYPE_ID)).thenThrow(new RoomTypeNotFoundException(ROOM_TYPE_ID));

        assertThatThrownBy(() -> roomService.createRoom(new CreateRoomCommand(101, ROOM_TYPE_ID)))
                .isInstanceOf(RoomTypeNotFoundException.class);

        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldCreateRoomWithRoomTypeFromRoomTypeService() {
        RoomType roomType = roomType("Double");
        when(roomRepository.existsByNumber(101)).thenReturn(false);
        when(roomTypeService.getRoomType(ROOM_TYPE_ID)).thenReturn(roomType);
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        Room result = roomService.createRoom(new CreateRoomCommand(101, ROOM_TYPE_ID));

        assertThat(result.getNumber()).isEqualTo(101);
        assertThat(result.getRoomType()).isSameAs(roomType);
    }

    // updateRoom

    @Test
    void shouldAssignNewRoomTypeWhenRoomTypeIdIsGiven() {
        Room room = room();
        RoomType newRoomType = roomType("Suite");
        UpdateRoomCommand command = new UpdateRoomCommand(null, ROOM_TYPE_ID);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomTypeService.getRoomType(ROOM_TYPE_ID)).thenReturn(newRoomType);
        when(roomRepository.save(room)).thenReturn(room);

        Room result = roomService.updateRoom(ROOM_ID, command);

        assertThat(result.getRoomType()).isSameAs(newRoomType);
    }

    @Test
    void shouldKeepRoomTypeWhenRoomTypeIdIsNull() {
        Room room = room();
        RoomType originalRoomType = room.getRoomType();
        UpdateRoomCommand command = new UpdateRoomCommand(null, null);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        Room result = roomService.updateRoom(ROOM_ID, command);

        assertThat(result.getRoomType()).isSameAs(originalRoomType);
        verify(roomTypeService, never()).getRoomType(any());
    }

    @Test
    void shouldRejectUpdatingRoomWithNumberOfAnotherRoom() {
        Room room = room();
        UpdateRoomCommand command = new UpdateRoomCommand(102, null);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndIdIsNot(102, ROOM_ID)).thenReturn(true);

        assertThatThrownBy(() -> roomService.updateRoom(ROOM_ID, command))
                .isInstanceOf(RoomAlreadyExistsException.class);

        verify(roomMapper, never()).updateFromCommand(any(), any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldSkipNumberCheckWhenUpdatingRoomWithNullNumber() {
        Room room = room();
        UpdateRoomCommand command = new UpdateRoomCommand(null, null);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        roomService.updateRoom(ROOM_ID, command);

        verify(roomRepository, never()).existsByNumberAndIdIsNot(any(), any());
        verify(roomMapper).updateFromCommand(command, room);
    }

    // getRoom

    @Test
    void shouldThrowWhenGettingUnknownRoom() {
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoom(ROOM_ID))
                .isInstanceOf(RoomNotFoundException.class);

        verify(roomRepository, never()).save(any());
    }

    // helpers

    private Room room() {
        Room room = new Room(101, roomType("Double"));
        ReflectionTestUtils.setField(room, "id", ROOM_ID);
        return room;
    }

    private RoomType roomType(String name) {
        RoomType roomType = new RoomType(name, 2, new BigDecimal("300.00"));
        ReflectionTestUtils.setField(roomType, "id", UUID.randomUUID());
        return roomType;
    }
}
