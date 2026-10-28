package com.bd.hotelapp.roomtype.repository;

import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdIsNot(String name, UUID id);
}
