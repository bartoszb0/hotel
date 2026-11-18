package com.bd.hotelapp.guest.service;

import com.bd.hotelapp.guest.domain.entity.Guest;

import java.util.UUID;

public interface GuestService {

    Guest findOrCreate(String firstName, String lastName, String email);

    Guest getGuest(UUID guestId);
}
