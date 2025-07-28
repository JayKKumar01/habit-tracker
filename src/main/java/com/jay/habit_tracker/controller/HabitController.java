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

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;
    private final JwtUtil jwtUtil;

    // ✅ Create habit (secured)
    @PostMapping("/create/{email}")
    public ResponseEntity<?> createHabit(
            @PathVariable String email,
            @RequestBody HabitRequest requestDTO,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        HabitResponse created = habitService.createHabit(email, requestDTO);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/edit/{email}")
    public ResponseEntity<?> editHabit(
            @PathVariable String email,
            @RequestBody HabitEditRequest editRequest,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        boolean updated = habitService.editHabit(editRequest);
        if (!updated) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied or habit not found"));
        }

        return ResponseEntity.ok(Map.of("message", "Habit edited", "response", editRequest.toString()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getHabitsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        Long tokenUserId = extractTokenUserId(request);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<HabitResponse> habits = habitService.getHabitsByUserId(userId);
        return ResponseEntity.ok(habits);
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
