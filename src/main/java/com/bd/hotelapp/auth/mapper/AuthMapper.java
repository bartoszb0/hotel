package com.bd.hotelapp.auth.mapper;

import com.bd.hotelapp.auth.domain.command.RegisterCommand;
import com.bd.hotelapp.auth.domain.dto.RegisterRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    RegisterCommand toCommand(RegisterRequest registerRequest);
}
