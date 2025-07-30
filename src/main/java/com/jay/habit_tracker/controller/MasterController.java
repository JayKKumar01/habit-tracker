package com.jay.habit_tracker.controller;
import com.jay.habit_tracker.dto.habit.HabitRequest;
import com.jay.habit_tracker.dto.habit.HabitResponse;
import com.jay.habit_tracker.dto.tag.TagDto;
import com.jay.habit_tracker.entity.*;
import com.jay.habit_tracker.enums.Frequency;
import com.jay.habit_tracker.repository.*;
import com.jay.habit_tracker.service.HabitService;
import com.jay.habit_tracker.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterController {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final ProfileRepository profileRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;
    private final HabitService habitService;
    private final TagService tagService;

    @DeleteMapping("/delete-users-except")
    public ResponseEntity<String> deleteAllUsersExcept(@RequestBody List<String> emailsToKeep) {
        List<User> usersToDelete = userRepository.findByEmailNotIn(emailsToKeep);
        for (User user: usersToDelete){
            System.out.println(user.getEmail()+", "+user.getName());
        }
        userRepository.deleteAll(usersToDelete);
        return ResponseEntity.ok("Deleted " + usersToDelete.size() + " users successfully.");
    }

    @PutMapping("/map-frequency-tags")
    @Transactional
    public ResponseEntity<String> mapTagsToHabitsByFrequency() {
        // Step 1: Fetch required tags by ID
        Tag dailyTag = tagRepository.findById(1L).orElseThrow(() ->
                new RuntimeException("Tag with ID 1 (DAILY) not found"));
        Tag weeklyTag = tagRepository.findById(3L).orElseThrow(() ->
                new RuntimeException("Tag with ID 3 (WEEKLY) not found"));

        // Step 2: Fetch habits by frequency
        List<Habit> dailyHabits = habitRepository.findByFrequency(Frequency.DAILY);
        List<Habit> weeklyHabits = habitRepository.findByFrequency(Frequency.WEEKLY);

        // Step 3: Assign tags if not already present
        int count = 0;
        for (Habit habit : dailyHabits) {
            if (!habit.getTags().contains(dailyTag)) {
                habit.getTags().add(dailyTag);
                count++;
            }
        }
        for (Habit habit : weeklyHabits) {
            if (!habit.getTags().contains(weeklyTag)) {
                habit.getTags().add(weeklyTag);
                count++;
            }
        }

        return ResponseEntity.ok("Mapped frequency tags to " + count + " habits.");
    }

    @DeleteMapping("/delete-tags-except")
    @Transactional
    public ResponseEntity<String> deleteTagsExcept(@RequestBody List<Long> idsToKeep) {
        List<Tag> tagsToDelete = tagRepository.findByIdNotIn(idsToKeep);

        for (Tag tag : tagsToDelete) {
            // Unlink tag from all associated habits
            for (Habit habit : tag.getHabits()) {
                habit.getTags().remove(tag);
            }
            tag.getHabits().clear(); // Optional: make sure all links are broken
        }

        tagRepository.deleteAll(tagsToDelete);
        return ResponseEntity.ok("Deleted " + tagsToDelete.size() + " tags successfully.");
    }



    @PostMapping("/create-default-tags")
    public ResponseEntity<List<Long>> createDefaultTags() {
        List<String> defaultTagNames = List.of(
                "Daily", "Weekly", "Morning", "Evening", "Health",
                "Fitness", "Work", "Meditation", "Study",
                "Productivity", "Personal", "Finance"
        );

        List<Long> tagIds = new ArrayList<>();

        for (String tagName : defaultTagNames) {
            String normalizedTag = tagName.trim().toLowerCase();

            Tag tag = tagRepository.findByName(normalizedTag)
                    .orElseGet(() -> tagRepository.save(
                            Tag.builder().name(normalizedTag).build()
                    ));

            tagIds.add(tag.getId());
        }

        return ResponseEntity.status(201).body(tagIds);
    }


    @PostMapping("/add-habit-tag/{habitId}")
    public ResponseEntity<?> addTagToHabit(
            @PathVariable Long habitId,
            @RequestParam String tagName) {

        TagDto habitTag = tagService.addHabitTag(habitId, tagName);

        if (habitTag == null) {
            return ResponseEntity
                    .status(409)
                    .body("Tag already exists for this habit.");
        }

        return ResponseEntity
                .status(201)
                .body(habitTag);
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

        // Habit Tags
        html.append("<h2>Habit Tags</h2><table border='1'><tr><th>Tag ID</th><th>Name</th><th>Habit ID</th></tr>");
        tagRepository.getAllProjectedTags().forEach(tag -> html.append("<tr>")
                .append("<td>").append(tag.getId()).append("</td>")
                .append("<td>").append(htmlEscape(tag.getName())).append("</td>")
                .append("<td>").append(tag.getHabitId()).append("</td>")
                .append("</tr>"));
        html.append("</table><br>");


        html.append("</body></html>");
        return ResponseEntity.ok().header("Content-Type", "text/html").body(html.toString());
    }

    private String htmlEscape(String input) {
        return HtmlUtils.htmlEscape(input == null ? "" : input);
    }
}
