package com.jay.habit_tracker.controller;
import com.jay.habit_tracker.dto.*;
import com.jay.habit_tracker.entity.*;
import com.jay.habit_tracker.mapper.*;
import com.jay.habit_tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterController {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final ProfileRepository profileRepository;
    private final UserMapper userMapper;
    private final HabitMapper habitMapper;
    private final HabitLogMapper habitLogMapper;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/rehash-passwords")
    public ResponseEntity<String> rehashAllPasswords() {
        List<User> users = userRepository.findAll();
        int count = 0;

        for (User user : users) {
            String currentPassword = user.getPassword();
            // Skip already-hashed passwords
            if (!currentPassword.startsWith("$2a$")) {
                String hashed = passwordEncoder.encode(currentPassword);
                user.setPassword(hashed);
                count++;
            }
        }

        userRepository.saveAll(users);
        return ResponseEntity.ok("Rehashed passwords for " + count + " users.");
    }


    @DeleteMapping("/delete-users-except")
    public ResponseEntity<String> deleteAllUsersExcept(@RequestBody DeleteUsers dto) {
        List<String> emailsToKeep = dto.getEmailsToKeep();
        List<User> usersToDelete = userRepository.findByEmailNotIn(emailsToKeep);
        userRepository.deleteAll(usersToDelete);
        return ResponseEntity.ok("Deleted " + usersToDelete.size() + " users successfully.");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/habits")
    public ResponseEntity<List<HabitResponse>> getAllHabits() {
        List<HabitResponse> habits = habitRepository.findAll().stream()
                .map(habitMapper::toDto)
                .collect(toList());

        return ResponseEntity.ok(habits);
    }

    // ✅ NEW: Get all habit logs
    @GetMapping("/habit-logs")
    public ResponseEntity<List<HabitLogResponse>> getAllHabitLogs() {
        List<HabitLogResponse> logs = habitLogRepository.findAll().stream()
                .map(habitLogMapper::toDto)
                .collect(toList());

        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/habits/{habitId}")
    public ResponseEntity<String> deleteHabitById(@PathVariable Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            return ResponseEntity.status(404).body("Habit with ID " + habitId + " not found.");
        }

        habitRepository.deleteById(habitId);
        return ResponseEntity.ok("Habit with ID " + habitId + " deleted successfully.");
    }

    @DeleteMapping("/habit-logs/{logId}")
    public ResponseEntity<String> deleteHabitLogById(@PathVariable Long logId) {
        if (!habitLogRepository.existsById(logId)) {
            return ResponseEntity.status(404).body("Habit log with ID " + logId + " not found.");
        }

        habitLogRepository.deleteById(logId);
        return ResponseEntity.ok("Habit log with ID " + logId + " deleted successfully.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(404).body("User with ID " + userId + " not found.");
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
    }

    @DeleteMapping("/habits/bulk")
    public ResponseEntity<String> deleteMultipleHabits(@RequestBody List<Long> habitIds) {
        List<Habit> habits = habitRepository.findAllById(habitIds);

        if (habits.isEmpty()) {
            return ResponseEntity.status(404).body("No habits found for the provided IDs.");
        }

        habitRepository.deleteAll(habits);
        return ResponseEntity.ok("Deleted " + habits.size() + " habits successfully.");
    }

    @GetMapping("/debug/native-view")
    public ResponseEntity<String> viewDbUsingProjections() {
        StringBuilder html = new StringBuilder("<html><body>");

        // Users
        html.append("<h2>Users</h2><table border='1'><tr><th>ID</th><th>Name</th><th>Email</th><th>Password</th><th>Created At</th></tr>");
        userRepository.getAllProjectedUsers().forEach(u -> html.append("<tr>")
                .append("<td>").append(u.getId()).append("</td>")
                .append("<td>").append(htmlEscape(u.getName())).append("</td>")
                .append("<td>").append(htmlEscape(u.getEmail())).append("</td>")
                .append("<td>").append(htmlEscape(u.getPassword())).append("</td>")
                .append("<td>").append(u.getCreatedAt()).append("</td>")
                .append("</tr>"));
        html.append("</table><br>");

        // Profiles
        html.append("<h2>Profiles</h2><table border='1'><tr><th>ID</th><th>Bio</th><th>User ID</th></tr>");
        profileRepository.getAllProjectedProfiles().forEach(p -> html.append("<tr>")
                .append("<td>").append(p.getId()).append("</td>")
                .append("<td>").append(htmlEscape(p.getBio())).append("</td>")
                .append("<td>").append(p.getUserId()).append("</td>")
                .append("</tr>"));
        html.append("</table><br>");

        // Habits
        html.append("<h2>Habits</h2><table border='1'><tr><th>ID</th><th>Title</th><th>Description</th><th>Frequency</th><th>Target Days</th><th>Start Date</th><th>End Date</th><th>Created At</th><th>User ID</th></tr>");
        habitRepository.getAllProjectedHabits().forEach(h -> {
            String targetDaysRaw = h.getTargetDays();
            String convertedDays = htmlEscape(convertTargetDays(targetDaysRaw));
            html.append("<tr>")
                    .append("<td>").append(h.getId()).append("</td>")
                    .append("<td>").append(htmlEscape(h.getTitle())).append("</td>")
                    .append("<td>").append(htmlEscape(h.getDescription())).append("</td>")
                    .append("<td>").append(htmlEscape(h.getFrequency())).append("</td>")
                    .append("<td>").append(convertedDays).append("</td>")
                    .append("<td>").append(h.getStartDate()).append("</td>")
                    .append("<td>").append(h.getEndDate()).append("</td>")
                    .append("<td>").append(h.getCreatedAt()).append("</td>")
                    .append("<td>").append(h.getUserId()).append("</td>")
                    .append("</tr>");
        });
        html.append("</table><br>");

        // Habit Logs
        html.append("<h2>Habit Logs</h2><table border='1'><tr><th>ID</th><th>Date</th><th>Completed</th><th>Habit ID</th></tr>");
        habitLogRepository.getAllProjectedLogs().forEach(l -> html.append("<tr>")
                .append("<td>").append(l.getId()).append("</td>")
                .append("<td>").append(l.getDate()).append("</td>")
                .append("<td>").append(l.getCompleted()).append("</td>")
                .append("<td>").append(l.getHabitId()).append("</td>")
                .append("</tr>"));
        html.append("</table><br>");

        html.append("</body></html>");
        return ResponseEntity.ok().header("Content-Type", "text/html").body(html.toString());
    }

    private String htmlEscape(String input) {
        return HtmlUtils.htmlEscape(input == null ? "" : input);
    }

    private String convertTargetDays(String raw) {
        if (raw == null || raw.isEmpty()) return "";

        try {
            // Clean, trim, remove blanks
            List<String> cleaned = Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            // Check if all parts are digits (integers)
            boolean allNumeric = cleaned.stream().allMatch(s -> s.matches("\\d+"));

            if (!allNumeric) {
                // Maybe already day names: sort alphabetically
                return cleaned.stream()
                        .map(String::toUpperCase)
                        .sorted(Comparator.comparingInt(this::dayOfWeekOrder))
                        .collect(Collectors.joining(","));
            }

            // Convert numbers to DayOfWeek and sort them properly
            return cleaned.stream()
                    .map(Integer::parseInt)
                    .map(i -> DayOfWeek.of((i % 7) + 1)) // 0=Sunday -> SUNDAY
                    .sorted(Comparator.comparingInt(DayOfWeek::getValue)) // Monday=1, ..., Sunday=7
                    .map(DayOfWeek::name)
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            return raw;
        }
    }

    // Helper: Natural day order
    private int dayOfWeekOrder(String day) {
        try {
            return DayOfWeek.valueOf(day.toUpperCase()).getValue(); // MONDAY = 1
        } catch (IllegalArgumentException e) {
            return 8; // unknown goes to end
        }
    }





}
