package com.bd.hotelapp.guest.repository;

import com.bd.hotelapp.guest.domain.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {

    Optional<Guest> findByEmail(String email);
}
