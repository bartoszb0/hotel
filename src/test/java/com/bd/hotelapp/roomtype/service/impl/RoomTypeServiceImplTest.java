package com.bd.hotelapp.roomtype.service.impl;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.command.UpdateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.exception.RoomTypeAlreadyExistsException;
import com.bd.hotelapp.roomtype.exception.RoomTypeNotFoundException;
import com.bd.hotelapp.roomtype.mapper.RoomTypeMapper;
import com.bd.hotelapp.roomtype.repository.RoomTypeRepository;
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
class RoomTypeServiceImplTest {

    private static final UUID ROOM_TYPE_ID = UUID.randomUUID();

    @Mock
    private RoomTypeRepository roomTypeRepository;
    @Mock
    private RoomTypeMapper roomTypeMapper;

    private RoomTypeServiceImpl roomTypeService;

    @BeforeEach
    void setUp() {
        roomTypeService = new RoomTypeServiceImpl(roomTypeRepository, roomTypeMapper);
    }

    // createRoomType

    @Test
    void shouldRejectCreatingRoomTypeWithDuplicateName() {
        CreateRoomTypeCommand command = new CreateRoomTypeCommand("Double", 2, new BigDecimal("300.00"));
        when(roomTypeRepository.existsByNameIgnoreCase("Double")).thenReturn(true);

        assertThatThrownBy(() -> roomTypeService.createRoomType(command))
                .isInstanceOf(RoomTypeAlreadyExistsException.class);

        verify(roomTypeRepository, never()).save(any());
    }

    @Test
    void shouldCreateRoomTypeWithGivenValues() {
        CreateRoomTypeCommand command = new CreateRoomTypeCommand("Double", 2, new BigDecimal("300.00"));
        when(roomTypeRepository.existsByNameIgnoreCase("Double")).thenReturn(false);
        when(roomTypeRepository.save(any(RoomType.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomType result = roomTypeService.createRoomType(command);

        assertThat(result.getName()).isEqualTo("Double");
        assertThat(result.getCapacity()).isEqualTo(2);
        assertThat(result.getPricePerNight()).isEqualByComparingTo("300.00");
        verify(roomTypeRepository).save(result);
    }

    // updateRoomType

    @Test
    void shouldUpdateRoomTypeWhenNameIsNotUsedByAnotherType() {
        RoomType roomType = roomType();
        UpdateRoomTypeCommand command = new UpdateRoomTypeCommand("Double", 3, null);
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.of(roomType));
        when(roomTypeRepository.existsByNameIgnoreCaseAndIdIsNot("Double", ROOM_TYPE_ID)).thenReturn(false);
        when(roomTypeRepository.save(roomType)).thenReturn(roomType);

        RoomType result = roomTypeService.updateRoomType(ROOM_TYPE_ID, command);

        assertThat(result).isSameAs(roomType);
        verify(roomTypeMapper).updateFromCommand(command, roomType);
    }

    @Test
    void shouldRejectUpdatingRoomTypeWithNameOfAnotherType() {
        RoomType roomType = roomType();
        UpdateRoomTypeCommand command = new UpdateRoomTypeCommand("Suite", null, null);
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.of(roomType));
        when(roomTypeRepository.existsByNameIgnoreCaseAndIdIsNot("Suite", ROOM_TYPE_ID)).thenReturn(true);

        assertThatThrownBy(() -> roomTypeService.updateRoomType(ROOM_TYPE_ID, command))
                .isInstanceOf(RoomTypeAlreadyExistsException.class);

        verify(roomTypeMapper, never()).updateFromCommand(any(), any());
        verify(roomTypeRepository, never()).save(any());
    }

    @Test
    void shouldSkipNameCheckWhenUpdatingRoomTypeWithNullName() {
        RoomType roomType = roomType();
        UpdateRoomTypeCommand command = new UpdateRoomTypeCommand(null, 3, null);
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.of(roomType));
        when(roomTypeRepository.save(roomType)).thenReturn(roomType);

        roomTypeService.updateRoomType(ROOM_TYPE_ID, command);

        verify(roomTypeRepository, never()).existsByNameIgnoreCaseAndIdIsNot(any(), any());
        verify(roomTypeMapper).updateFromCommand(command, roomType);
    }

    // getRoomType

    @Test
    void shouldThrowWhenGettingUnknownRoomType() {
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomTypeService.getRoomType(ROOM_TYPE_ID))
                .isInstanceOf(RoomTypeNotFoundException.class);

        verify(roomTypeRepository, never()).save(any());
    }

    // deleteRoomType

    @Test
    void shouldThrowWhenDeletingUnknownRoomType() {
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomTypeService.deleteRoomType(ROOM_TYPE_ID))
                .isInstanceOf(RoomTypeNotFoundException.class);

        verify(roomTypeRepository, never()).delete(any());
        verify(roomTypeRepository, never()).save(any());
    }

    // helpers

    private RoomType roomType() {
        RoomType roomType = new RoomType("Double", 2, new BigDecimal("300.00"));
        ReflectionTestUtils.setField(roomType, "id", ROOM_TYPE_ID);
        return roomType;
    }
}
