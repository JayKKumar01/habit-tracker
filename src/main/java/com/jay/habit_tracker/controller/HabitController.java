package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.dto.HabitSoftDeleteRequestDto;
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

    @PutMapping("/soft-delete/{email}")
    public ResponseEntity<?> softDeleteHabit(
            @PathVariable String email,
            @RequestBody HabitSoftDeleteRequestDto deleteDto,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        boolean updated = habitService.softDeleteHabitByIdForUser(deleteDto.getId(), email, deleteDto.getEndDate());
        if (!updated) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied or habit not found"));
        }

        return ResponseEntity.ok(Map.of("message", "Habit marked as ended.", "endDate", deleteDto.getEndDate()));
    }



    // ✅ Get user habits (secured)
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserHabits(@PathVariable String email, HttpServletRequest request) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<HabitResponse> habits = habitService.getHabitsByUser(email);
        return ResponseEntity.ok(habits);
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
}
