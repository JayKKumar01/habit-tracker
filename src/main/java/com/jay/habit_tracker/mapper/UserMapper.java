package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;
import com.jay.habit_tracker.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);                            // for responses
    User toEntity(UserDto dto);                          // unused for now but okay to keep
    User toEntity(UserRegistrationDto dto);              // new: for user creation
}
