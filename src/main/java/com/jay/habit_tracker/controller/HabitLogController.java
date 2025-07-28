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

//    // ✅ NEW: Get all logs for a user (used to batch fetch logs for all habits)
//    @GetMapping("/all/{email}")
//    public ResponseEntity<?> getAllLogsForUser(
//            @PathVariable String email,
//            HttpServletRequest request
//    ) {
//        String tokenEmail = extractTokenEmail(request);
//        if (tokenEmail == null || !tokenEmail.equals(email)) {
//            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
//        }
//
//        List<HabitLogResponse> userLogs = habitLogService.getAllLogsForUser(email);
//        return ResponseEntity.ok(userLogs);
//    }

//     ✅ NEW: Get all logs for a user (used to batch fetch logs for all habits)
    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllLogsForUserId(@PathVariable Long userId, HttpServletRequest request) {
        Long tokenUserId = extractTokenUserId(request);
        if (tokenUserId == null || !tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<HabitLogResponse> userLogs = habitLogService.getAllLogsForUserId(userId);
        return ResponseEntity.ok(userLogs);
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
