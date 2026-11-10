package com.bd.hotelapp.room.repository;

import com.bd.hotelapp.room.domain.entity.Room;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {

    boolean existsByNumber(Integer number);

    boolean existsByNumberAndIdIsNot(Integer number, UUID id);

    @Override
    @EntityGraph(attributePaths = "roomType")
    List<Room> findAll();

    @Override
    @EntityGraph(attributePaths = "roomType")
    Optional<Room> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = "roomType")
    List<Room> findAll(Specification<Room> spec);
}
