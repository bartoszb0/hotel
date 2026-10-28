package com.bd.hotelapp.roomtype.service.impl;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.exception.RoomTypeAlreadyExistsException;
import com.bd.hotelapp.roomtype.exception.RoomTypeNotFoundException;
import com.bd.hotelapp.roomtype.repository.RoomTypeRepository;
import com.bd.hotelapp.roomtype.service.RoomTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    public List<RoomType> getRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    public RoomType getRoomType(UUID id) {
        return roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
    }

    @Override
    public RoomType createRoomType(CreateRoomTypeCommand createRoomTypeCommand) {
        if (roomTypeRepository.existsByNameIgnoreCase(createRoomTypeCommand.name())) {
            throw new RoomTypeAlreadyExistsException(createRoomTypeCommand.name());
        }

        RoomType roomType = new RoomType(
                createRoomTypeCommand.name(),
                createRoomTypeCommand.capacity(),
                createRoomTypeCommand.pricePerNight()
        );

        return roomTypeRepository.save(roomType);
    }

    @Override
    public void deleteRoomType(UUID id) {
        RoomType roomType = getRoomType(id);
        roomTypeRepository.delete(roomType);
    }
}
