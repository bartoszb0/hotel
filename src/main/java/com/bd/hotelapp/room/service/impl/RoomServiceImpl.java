package com.bd.hotelapp.room.service.impl;

import com.bd.hotelapp.room.domain.command.CreateRoomCommand;
import com.bd.hotelapp.room.domain.command.UpdateRoomCommand;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.exception.RoomAlreadyExistsException;
import com.bd.hotelapp.room.exception.RoomNotFoundException;
import com.bd.hotelapp.room.mapper.RoomMapper;
import com.bd.hotelapp.room.repository.RoomRepository;
import com.bd.hotelapp.room.service.RoomService;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.service.RoomTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeService roomTypeService;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository, RoomMapper roomMapper, RoomTypeService roomTypeService) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.roomTypeService = roomTypeService;
    }

    @Override
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoom(UUID id) {
        return roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
    }

    @Override
    @Transactional
    public Room createRoom(CreateRoomCommand createRoomCommand) {
        if (roomRepository.existsByNumber(createRoomCommand.number())) {
            throw new RoomAlreadyExistsException(createRoomCommand.number());
        }

        Room room = new Room(
                createRoomCommand.number(),
                roomTypeService.getRoomType(createRoomCommand.roomTypeId())
        );

        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room updateRoom(UUID id, UpdateRoomCommand updateRoomCommand) {
        Room room = getRoom(id);

        if (updateRoomCommand.number() != null
                && roomRepository.existsByNumberAndIdIsNot(updateRoomCommand.number(), id)) {
            throw new RoomAlreadyExistsException(updateRoomCommand.number());
        }

        roomMapper.updateFromCommand(updateRoomCommand, room);

        if (updateRoomCommand.roomTypeId() != null) {
            RoomType newRoomType = roomTypeService.getRoomType(updateRoomCommand.roomTypeId());
            room.setRoomType(newRoomType);
        }

        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(UUID id) {
        Room room = getRoom(id);
        roomRepository.delete(room);
    }
}
