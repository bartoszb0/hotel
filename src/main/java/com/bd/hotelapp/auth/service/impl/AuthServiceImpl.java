package com.bd.hotelapp.auth.service.impl;

import com.bd.hotelapp.auth.domain.command.LoginCommand;
import com.bd.hotelapp.auth.domain.command.RegisterCommand;
import com.bd.hotelapp.auth.domain.dto.AuthResponse;
import com.bd.hotelapp.auth.service.AuthService;
import com.bd.hotelapp.common.security.JwtTokenService;
import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.service.GuestService;
import com.bd.hotelapp.user.domain.entity.User;
import com.bd.hotelapp.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final GuestService guestService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(GuestService guestService, UserService userService, AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.guestService = guestService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
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

    @Override
    public AuthResponse login(LoginCommand loginCommand) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginCommand.email(), loginCommand.password())
        );

        String role = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwtTokenService.generateToken(auth.getName(), role);

        return new AuthResponse(token, "Bearer", jwtTokenService.getExpiration().toSeconds());
    }
}
