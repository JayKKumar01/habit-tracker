package com.jay.habit_tracker.service;


import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserLoginDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByEmail(String email);
    boolean deleteUserByEmail(String email);
}
