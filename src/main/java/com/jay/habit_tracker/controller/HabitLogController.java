package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.HabitLogRequest;
import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.service.HabitLogService;
import com.jay.habit_tracker.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habit-log")
@RequiredArgsConstructor
public class HabitLogController {

    private final HabitLogService habitLogService;
    private final JwtUtil jwtUtil;

    @PostMapping("/update/{userId}")
    public ResponseEntity<?> updateHabitLog(
            @PathVariable Long userId,
            @RequestBody HabitLogRequest habitLogRequest,
            HttpServletRequest request
    ) {
        Long tokenUserId = extractTokenUserId(request); // You’ll implement this
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        HabitLogResponse updatedLog = habitLogService.updateHabitLog(habitLogRequest);
        if (updatedLog == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Habit not found or not owned by user"));
        }

        return ResponseEntity.ok(updatedLog);
    }


    // ✅ Reuse token-email extraction
    private Long extractTokenUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}
