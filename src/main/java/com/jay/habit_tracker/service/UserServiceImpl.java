package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserLoginDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;
import com.jay.habit_tracker.entity.User;
import com.jay.habit_tracker.mapper.UserMapper;
import com.jay.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserRegistrationDto userDto) {
        User user = userMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }



    @Override
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public UserDto loginUser(UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // For now, we use plain text password comparison (we'll use BCrypt later)
        if (!user.getPassword().equals(userLoginDto.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return userMapper.toDto(user);
    }


}
