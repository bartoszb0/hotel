package com.bd.hotelapp.auth.service;

import com.bd.hotelapp.auth.domain.command.LoginCommand;
import com.bd.hotelapp.auth.domain.command.RegisterCommand;
import com.bd.hotelapp.auth.domain.dto.AuthResponse;
import com.bd.hotelapp.user.domain.entity.User;

public interface AuthService {

    User register(RegisterCommand registerCommand);

    AuthResponse login(LoginCommand loginCommand);
}
