package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.HabitRequestDTO;
import com.jay.habit_tracker.dto.HabitResponseDTO;
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
            @RequestBody HabitRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        HabitResponseDTO created = habitService.createHabit(email, requestDTO);
        return ResponseEntity.status(201).body(created);
    }

    // ✅ Get user habits (secured)
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserHabits(@PathVariable String email, HttpServletRequest request) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<HabitResponseDTO> habits = habitService.getHabitsByUser(email);
        return ResponseEntity.ok(habits);
    }

    // ✅ Secured version
    @GetMapping("/{id}")
    public ResponseEntity<?> getHabit(@PathVariable Long id, HttpServletRequest request) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
        }

        HabitResponseDTO habit = habitService.getHabitByIdForUser(id, tokenEmail);
        if (habit == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied or habit not found"));
        }

        return ResponseEntity.ok(habit);
    }


    // ✅ Delete habit (secured based on token-owner match)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable Long id, HttpServletRequest request) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
        }

        boolean deleted = habitService.deleteHabitForUser(id, tokenEmail);
        if (!deleted) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied or habit not found"));
        }

        return ResponseEntity.noContent().build();
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
