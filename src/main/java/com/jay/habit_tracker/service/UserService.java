package com.jay.habit_tracker.service;


import com.jay.habit_tracker.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    void deleteUser(Long id);
}
