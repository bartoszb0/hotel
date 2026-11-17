package com.bd.hotelapp.auth.service.impl;

import com.bd.hotelapp.auth.domain.command.RegisterCommand;
import com.bd.hotelapp.auth.service.AuthService;
import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.service.GuestService;
import com.bd.hotelapp.user.domain.entity.User;
import com.bd.hotelapp.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final GuestService guestService;
    private final UserService userService;

    public AuthServiceImpl(GuestService guestService, UserService userService) {
        this.guestService = guestService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public User register(RegisterCommand registerCommand) {
        Guest guest = guestService.findOrCreate(
                registerCommand.firstName(),
                registerCommand.lastName(),
                registerCommand.email()
        );
        return userService.createGuestAccount(
                registerCommand.email(),
                registerCommand.password(),
                guest
        );
    }
}
