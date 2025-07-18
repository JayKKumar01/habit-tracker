package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.AuthRequest;
import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.UserMapper;
import com.jay.habit_tracker.repository.UserRepository;
import com.jay.habit_tracker.service.AuthService;
import com.jay.habit_tracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public UserDto signup(UserRegistrationDto dto) {
        User user = userMapper.toEntity(dto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public String login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(user);
    }
}
