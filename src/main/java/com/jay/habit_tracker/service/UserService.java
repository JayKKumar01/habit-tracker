package com.jay.habit_tracker.service;


import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserRegistrationDto userRegistrationDto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    void deleteUser(Long id);
}
