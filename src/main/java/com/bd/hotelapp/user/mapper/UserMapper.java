package com.bd.hotelapp.user.mapper;

import com.bd.hotelapp.user.domain.dto.UserResponse;
import com.bd.hotelapp.user.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
