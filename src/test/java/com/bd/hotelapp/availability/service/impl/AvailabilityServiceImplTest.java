package com.bd.hotelapp.availability.service.impl;

import com.bd.hotelapp.availability.exception.InvalidSearchDatesException;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.service.RoomService;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    private static final LocalDate FROM = LocalDate.of(2026, 3, 10);

    @Mock
    private RoomService roomService;

    private AvailabilityServiceImpl availabilityService;

    @BeforeEach
    void setUp() {
        availabilityService = new AvailabilityServiceImpl(roomService);
    }

    @Test
    void shouldThrowWhenOnlyFromDateGiven() {
        assertThatThrownBy(() -> availabilityService.searchRooms(null, null, null, FROM, null))
                .isInstanceOf(InvalidSearchDatesException.class);

        verify(roomService, never()).findRooms(any());
    }

    @Test
    void shouldThrowWhenOnlyToDateGiven() {
        assertThatThrownBy(() -> availabilityService.searchRooms(null, null, null, null, FROM.plusDays(1)))
                .isInstanceOf(InvalidSearchDatesException.class);

        verify(roomService, never()).findRooms(any());
    }

    @Test
    void shouldThrowWhenToDateBeforeFromDate() {
        assertThatThrownBy(() -> availabilityService.searchRooms(null, null, null, FROM, FROM.minusDays(1)))
                .isInstanceOf(InvalidSearchDatesException.class);
    }

    @Test
    void shouldThrowWhenToDateEqualsFromDate() {
        assertThatThrownBy(() -> availabilityService.searchRooms(null, null, null, FROM, FROM))
                .isInstanceOf(InvalidSearchDatesException.class);
    }

    @Test
    void shouldSearchRoomsWhenToDateIsOneDayAfterFromDate() {
        List<Room> rooms = List.of(new Room(101, new RoomType("Double", 2, new BigDecimal("300.00"))));
        when(roomService.findRooms(any())).thenReturn(rooms);

        List<Room> result = availabilityService.searchRooms(null, null, null, FROM, FROM.plusDays(1));

        assertThat(result).isSameAs(rooms);
        verify(roomService, times(1)).findRooms(any());
    }

    @Test
    void shouldSearchRoomsWhenBothDatesNull() {
        when(roomService.findRooms(any())).thenReturn(List.of());

        availabilityService.searchRooms(null, null, null, null, null);

        verify(roomService, times(1)).findRooms(any());
    }
}
