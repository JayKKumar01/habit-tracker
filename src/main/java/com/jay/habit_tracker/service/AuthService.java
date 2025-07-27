package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.AuthRequest;
import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserRegistration;

public interface AuthService {
    UserDto signup(UserRegistration dto);
    String login(AuthRequest request);
}
