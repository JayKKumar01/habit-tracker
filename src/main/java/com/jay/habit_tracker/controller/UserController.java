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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        UserDto createdUser = userService.createUser(userRegistrationDto);
        return ResponseEntity
                .status(201)
                .body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserDto dto = userService.getUserById(id);

        if (dto == null) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "User with ID " + id + " not found.")
            );
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String email,
                                      HttpServletRequest request) {
        if (email != null && !email.isEmpty()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
            }

            String token = authHeader.substring(7);
            String tokenEmail = jwtUtil.extractEmail(token);

            if (!email.equals(tokenEmail)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            UserDto userDto = userService.getUserByEmail(email);
            if (userDto == null) {
                return ResponseEntity.status(404).body(Map.of("message", "User with email " + email + " not found."));
            }
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(403).body(Map.of("error", "Access to all users is not allowed."));
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).body(
                    Map.of("message", "User with ID " + id + " not found.")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userLoginDto) {
        try {
            UserDto user = userService.loginUser(userLoginDto);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", e.getMessage())
            );
        }
    }


}
