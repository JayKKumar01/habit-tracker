package com.jay.habit_tracker.controller;

import com.jay.habit_tracker.dto.ProfileRequest;
import com.jay.habit_tracker.dto.ProfileResponse;
import com.jay.habit_tracker.service.ProfileService;
import com.jay.habit_tracker.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final JwtUtil jwtUtil;

    // ✅ Create or update profile bio (secured)
    @PostMapping("/save/{email}")
    public ResponseEntity<?> saveOrUpdateProfile(
            @PathVariable String email,
            @RequestBody ProfileRequest profileRequest,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        ProfileResponse response = profileService.saveOrUpdate(email, profileRequest);
        return ResponseEntity.ok(response);
    }

    // ✅ Get profile by user email (secured)
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getProfile(
            @PathVariable String email,
            HttpServletRequest request
    ) {
        String tokenEmail = extractTokenEmail(request);
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        ProfileResponse response = profileService.getProfileByEmail(email);
        return ResponseEntity.ok(response);
    }

    // ✅ Extract token email
    private String extractTokenEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractEmail(token);
    }
}
