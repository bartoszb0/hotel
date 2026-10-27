package com.bd.hotelapp.room.domain.entity;

import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "number", nullable = false)
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    protected Room() {
    }

    public Room(Integer number, RoomType roomType) {
        this.number = number;
        this.roomType = roomType;
    }

    public UUID getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}
