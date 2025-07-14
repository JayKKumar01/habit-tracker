package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.AuthRequest;
import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;

public interface AuthService {
    UserDto signup(UserRegistrationDto dto);
    String login(AuthRequest request);
}
