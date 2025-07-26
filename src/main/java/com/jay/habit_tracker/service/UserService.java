package com.jay.habit_tracker.service;


import com.jay.habit_tracker.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByEmail(String email);
    boolean deleteUserByEmail(String email);
}
