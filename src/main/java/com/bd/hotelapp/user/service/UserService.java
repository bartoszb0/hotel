package com.bd.hotelapp.user.service;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.user.domain.entity.User;

import java.util.UUID;

public interface UserService {

    User createGuestAccount(String email, String rawPassword, Guest guest);

    User createStaffAccount(String email, String rawPassword);

    User getUserByEmail(String email);

    UUID getGuestId(UUID userId);
}
