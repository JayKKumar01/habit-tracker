package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.HabitLogRequest;
import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.entity.HabitLog;
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

    // ✅ Update or Create Habit Log (secured)
    @PostMapping("/update/{email}")
    public ResponseEntity<?> updateHabitLog(
            @PathVariable String email,
            @RequestBody HabitLogRequest habitLogRequest,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        HabitLogResponse updatedLog = habitLogService.updateHabitLog(habitLogRequest);
        if (updatedLog == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Habit not found or not owned by user"));
        }

        return ResponseEntity.ok(updatedLog);
    }

    // ✅ Get all habit logs for a habit (secured)
    @GetMapping("/all/{email}/{habitId}")
    public ResponseEntity<?> getAllHabitLogs(
            @PathVariable String email,
            @PathVariable Long habitId,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<HabitLogResponse> habitLogs = habitLogService.getAllLogsForHabit(habitId);
        return ResponseEntity.ok(habitLogs);
    }


    // ✅ Reuse token-email extraction
    private String extractTokenEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractEmail(token);
    }
}
