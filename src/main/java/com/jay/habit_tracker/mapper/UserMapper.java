package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}
