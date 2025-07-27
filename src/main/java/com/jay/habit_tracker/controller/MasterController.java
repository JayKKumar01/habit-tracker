package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.*;
import com.jay.habit_tracker.entity.*;
import com.jay.habit_tracker.mapper.*;
import com.jay.habit_tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterController {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserMapper userMapper;
    private final HabitMapper habitMapper;
    private final HabitLogMapper habitLogMapper;

    @DeleteMapping("/delete-users-except")
    public ResponseEntity<String> deleteAllUsersExcept(@RequestBody DeleteUsers dto) {
        List<String> emailsToKeep = dto.getEmailsToKeep();
        List<User> usersToDelete = userRepository.findByEmailNotIn(emailsToKeep);
        userRepository.deleteAll(usersToDelete);
        return ResponseEntity.ok("Deleted " + usersToDelete.size() + " users successfully.");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/habits")
    public ResponseEntity<List<HabitResponse>> getAllHabits() {
        List<HabitResponse> habits = habitRepository.findAll().stream()
                .map(habitMapper::toDto)
                .collect(toList());

        return ResponseEntity.ok(habits);
    }

    // ✅ NEW: Get all habit logs
    @GetMapping("/habit-logs")
    public ResponseEntity<List<HabitLogResponse>> getAllHabitLogs() {
        List<HabitLogResponse> logs = habitLogRepository.findAll().stream()
                .map(habitLogMapper::toDto)
                .collect(toList());

        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/habits/{habitId}")
    public ResponseEntity<String> deleteHabitById(@PathVariable Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            return ResponseEntity.status(404).body("Habit with ID " + habitId + " not found.");
        }

        habitRepository.deleteById(habitId);
        return ResponseEntity.ok("Habit with ID " + habitId + " deleted successfully.");
    }

    @DeleteMapping("/habit-logs/{logId}")
    public ResponseEntity<String> deleteHabitLogById(@PathVariable Long logId) {
        if (!habitLogRepository.existsById(logId)) {
            return ResponseEntity.status(404).body("Habit log with ID " + logId + " not found.");
        }

        habitLogRepository.deleteById(logId);
        return ResponseEntity.ok("Habit log with ID " + logId + " deleted successfully.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(404).body("User with ID " + userId + " not found.");
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
    }

    @DeleteMapping("/habits/bulk")
    public ResponseEntity<String> deleteMultipleHabits(@RequestBody List<Long> habitIds) {
        List<Habit> habits = habitRepository.findAllById(habitIds);

        if (habits.isEmpty()) {
            return ResponseEntity.status(404).body("No habits found for the provided IDs.");
        }

        habitRepository.deleteAll(habits);
        return ResponseEntity.ok("Deleted " + habits.size() + " habits successfully.");
    }

}
