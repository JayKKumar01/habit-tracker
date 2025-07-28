package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.AuthRequest;
import com.jay.habit_tracker.dto.AuthResponse;
import com.jay.habit_tracker.dto.user.UserRegistration;
import com.jay.habit_tracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRegistration dto) {
        return ResponseEntity.status(201).body(authService.signup(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
