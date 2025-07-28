package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.*;
import com.jay.habit_tracker.service.HabitService;
import com.jay.habit_tracker.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;
    private final JwtUtil jwtUtil;

    // ✅ Create habit (secured)
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createHabit(@PathVariable Long userId, @RequestBody HabitRequest requestDTO, HttpServletRequest request) {
        Long tokenUserId = extractTokenUserId(request);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        HabitResponse created = habitService.createHabit(userId, requestDTO);
        return ResponseEntity.status(201).body(created);
    }

    // ✅ Edit habit (secured)
    @PutMapping("/edit/{userId}")
    public ResponseEntity<?> editHabit(@PathVariable Long userId, @RequestBody HabitEditRequest editRequest, HttpServletRequest request) {
        Long tokenUserId = extractTokenUserId(request);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        HabitResponse updated = habitService.editHabit(editRequest);
        return ResponseEntity.ok(updated); // 200 OK for updates
    }



    @GetMapping("/habitsAndLogs/{userId}")
    public ResponseEntity<?> getHabitWithLogsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        Long tokenUserId = extractTokenUserId(request);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<HabitWithLogsResponse> habits = habitService.getHabitWithLogsByUserId(userId);
        return ResponseEntity.ok(habits);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteHabit(
            @PathVariable String email,
            @RequestBody HabitDeleteRequest deleteRequest,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        boolean deleted = habitService.deleteHabit(deleteRequest.getHabitId());
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("error", "Habit not found or not authorized"));
        }

        return ResponseEntity.ok(Map.of("message", "Habit deleted", "habitId", deleteRequest.getHabitId()));
    }

    // ✅ Extract token email like in UserController
    private String extractTokenEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractEmail(token);
    }

    // ✅ Extract token email like in UserController
    private Long extractTokenUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}
