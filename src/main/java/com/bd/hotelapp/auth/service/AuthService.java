package com.bd.hotelapp.auth.service;

import com.bd.hotelapp.auth.domain.command.RegisterCommand;
import com.bd.hotelapp.user.domain.entity.User;

public interface AuthService {

    User register(RegisterCommand registerCommand);
}
