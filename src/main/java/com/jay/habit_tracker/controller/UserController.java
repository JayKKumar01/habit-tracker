package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.UserDto;
import com.jay.habit_tracker.dto.UserLoginDto;
import com.jay.habit_tracker.dto.UserRegistrationDto;
import com.jay.habit_tracker.service.UserService;
import com.jay.habit_tracker.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getUserByEmail(@RequestParam String email, HttpServletRequest request) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        UserDto userDto = userService.getUserByEmail(email);
        if (userDto == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User with email " + email + " not found."));
        }

        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserByEmail(@RequestParam String email, HttpServletRequest request) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        boolean deleted = userService.deleteUserByEmail(email);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "User with email " + email + " not found."));
        }

        return ResponseEntity.noContent().build();
    }

    private String extractTokenEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractEmail(token);
    }
}
