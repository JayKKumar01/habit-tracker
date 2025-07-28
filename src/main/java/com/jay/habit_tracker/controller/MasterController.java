package com.jay.habit_tracker.controller;
import com.jay.habit_tracker.dto.*;
import com.jay.habit_tracker.entity.*;
import com.jay.habit_tracker.enums.Frequency;
import com.jay.habit_tracker.mapper.*;
import com.jay.habit_tracker.repository.*;
import com.jay.habit_tracker.service.HabitService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
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
    private final HabitService habitService;


    @PersistenceContext
    private EntityManager entityManager;

    @DeleteMapping("/deleteHabit/{habitId}")
    public ResponseEntity<?> deleteHabitWithoutAuth(@PathVariable Long habitId) {
        try {
            Habit habitRef = entityManager.getReference(Habit.class, habitId);
            habitRepository.delete(habitRef);
            return ResponseEntity.ok(Map.of("message", "Habit deleted", "habitId", habitId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Habit not found", "habitId", habitId));
        }
    }



    @PostMapping("/createHabit/{userId}/{count}")
    public ResponseEntity<List<HabitResponse>> createHabitWithoutAuth(
            @PathVariable Long userId,
            @PathVariable int count) {

        List<HabitResponse> createdHabits = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            System.out.println("Creating Habit #" + i);

            HabitRequest requestDTO = new HabitRequest();
            requestDTO.setTitle("Habit " + i);
            requestDTO.setDescription("Description for Habit " + i);
            requestDTO.setFrequency(Frequency.DAILY);
            requestDTO.setTargetDays(Set.of(
                    "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                    "FRIDAY", "SATURDAY", "SUNDAY"
            ));
            requestDTO.setStartDate(LocalDate.parse("2025-07-28"));

            HabitResponse response = habitService.createHabit(userId, requestDTO);
            createdHabits.add(response);
        }

        return ResponseEntity.status(201).body(createdHabits);
    }

    @PostMapping("/editHabit/{habitId}")
    public ResponseEntity<?> editHabitWithoutAuth(
            @PathVariable Long habitId,
            @RequestParam(required = false) String endDateStr) {

        System.out.println("Editing Habit ID: " + habitId);

        HabitEditRequest editRequest = new HabitEditRequest();
        editRequest.setHabitId(habitId);
        editRequest.setTitle("Updated Title " + habitId);
        editRequest.setDescription("Updated Description for Habit " + habitId);

        if (endDateStr != null && !endDateStr.isBlank()) {
            editRequest.setEndDate(LocalDate.parse(endDateStr));
        }

        HabitEditResponse updatedHabit = habitService.editHabit(editRequest);
        return ResponseEntity.ok(updatedHabit);
    }







    @DeleteMapping("/drop-old-target-days-table")
    @Transactional
    public ResponseEntity<String> dropOldTargetDaysTable() {
        try {
            entityManager
                    .createNativeQuery("DROP TABLE IF EXISTS habit_target_days")
                    .executeUpdate();

            return ResponseEntity.ok("Old table 'habit_target_days' dropped successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error dropping table: " + e.getMessage());
        }
    }

    @PutMapping("/update-target-days-bulk")
    public ResponseEntity<String> updateTargetDaysInBulk(@RequestBody Map<Long, String> habitDayMap) {
        Map<Integer, String> dayMap = Map.of(
                0, "MONDAY",
                1, "TUESDAY",
                2, "WEDNESDAY",
                3, "THURSDAY",
                4, "FRIDAY",
                5, "SATURDAY",
                6, "SUNDAY"
        );

        for (Map.Entry<Long, String> entry : habitDayMap.entrySet()) {
            Long habitId = entry.getKey();
            String dayIndexes = entry.getValue();

            Set<String> targetDays = Arrays.stream(dayIndexes.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .sorted() // keep order Mon–Sun
                    .map(dayMap::get)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            Habit habit = habitRepository.findById(habitId)
                    .orElseThrow(() -> new RuntimeException("Habit not found with id: " + habitId));

            habit.setTargetDays(targetDays);
            habitRepository.save(habit);
        }

        return ResponseEntity.ok("Target days updated successfully for " + habitDayMap.size() + " habits.");
    }


    @PutMapping("/habits/update-target-days")
    public ResponseEntity<String> updateHabitTargetDays(@RequestBody HabitEditRequestTargetDays dto) {
        Optional<Habit> optionalHabit = habitRepository.findById(dto.getHabitId());

        if (optionalHabit.isEmpty()) {
            return ResponseEntity.status(404).body("Habit with ID " + dto.getHabitId() + " not found.");
        }

        Habit habit = optionalHabit.get();
        habit.setTargetDays(dto.getTargetDays());
        habitRepository.save(habit);

        return ResponseEntity.ok("Updated targetDays for habit ID " + dto.getHabitId());
    }


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
            String convertedDays = htmlEscape(targetDaysRaw);
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






}
