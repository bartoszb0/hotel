package com.bd.hotelapp.user.service;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.user.domain.entity.User;

public interface UserService {

    User createGuestAccount(String email, String rawPassword, Guest guest);

    User createStaffAccount(String email, String rawPassword);
}
