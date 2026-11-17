package com.bd.hotelapp.auth.controller;

import com.bd.hotelapp.auth.domain.command.RegisterCommand;
import com.bd.hotelapp.auth.domain.dto.RegisterRequest;
import com.bd.hotelapp.auth.mapper.AuthMapper;
import com.bd.hotelapp.auth.service.AuthService;
import com.bd.hotelapp.common.web.ApiPaths;
import com.bd.hotelapp.user.domain.dto.UserResponse;
import com.bd.hotelapp.user.domain.entity.User;
import com.bd.hotelapp.user.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.V1 + "/auth")
public class AuthController {

    private final AuthMapper authMapper;
    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthMapper authMapper, AuthService authService, UserMapper userMapper) {
        this.authMapper = authMapper;
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        RegisterCommand registerCommand = authMapper.toCommand(registerRequest);
        User user = authService.register(registerCommand);
        UserResponse userResponse = userMapper.toResponse(user);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}
