package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.user.UserResponse;
import com.jay.habit_tracker.mapper.UserMapper;
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public boolean deleteUserByEmail(String email) {
        return userRepository.findByEmail(email).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }


}
