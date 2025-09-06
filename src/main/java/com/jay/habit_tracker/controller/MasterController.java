//package com.jay.habit_tracker.controller;
//
//import com.jay.habit_tracker.entity.User;
//import com.jay.habit_tracker.repository.UserRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.util.HtmlUtils;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/master")
//@RequiredArgsConstructor
//@SuppressWarnings("unchecked")
//public class MasterController {
//
//    private final UserRepository userRepository;
//
//    @PersistenceContext
//    private final EntityManager entityManager;
//
//    /**
//     * Delete all users except the ones whose emails are passed in the request body.
//     */
//    @DeleteMapping("/delete-users-except")
//    public ResponseEntity<String> deleteAllUsersExcept(@RequestBody List<String> emailsToKeep) {
//        if (emailsToKeep == null || emailsToKeep.isEmpty()) {
//            return ResponseEntity.badRequest().body("emailsToKeep list cannot be empty.");
//        }
//
//        // Step 1: Fetch users to delete using native query
//        String inClause = String.join(",", emailsToKeep.stream().map(email -> "'" + email + "'").toList());
//
//        String sql = "SELECT * FROM users WHERE email NOT IN (" + inClause + ")";
//        List<User> usersToDelete = entityManager.createNativeQuery(sql, User.class).getResultList();
//
//        // Step 2: Log (optional)
//        for (User user : usersToDelete) {
//            System.out.println("Deleting user: " + user.getEmail() + " - " + user.getName());
//        }
//
//        // Step 3: Delete them using repository
//        userRepository.deleteAll(usersToDelete);
//
//        return ResponseEntity.ok("Deleted " + usersToDelete.size() + " users successfully.");
//    }
//
//
//    /**
//     * View all DB tables in HTML format using native queries.
//     */
//    @GetMapping("/debug/native-view")
//    public ResponseEntity<String> viewDbUsingNativeSQL() {
//
//        // Users
//
//        String html = "<html><body>" + buildTable("Users", "SELECT id, name, email, password, created_at FROM users",
//                "ID", "Name", "Email", "Password", "Created At") +
//
//                // Profiles
//                buildTable("Profiles", "SELECT id, bio, user_id FROM profiles",
//                        "ID", "Bio", "User ID") +
//
//                // Habits (with target days)
//                buildTable("Habits", """
//                            SELECT h.id, h.title, h.description, h.frequency,
//                                   GROUP_CONCAT(htds.target_day ORDER BY htds.target_day SEPARATOR ', ') AS target_days,
//                                   h.start_date, h.end_date, h.created_at, h.user_id
//                            FROM habits h
//                            LEFT JOIN habit_target_day_strings htds ON h.id = htds.habit_id
//                            GROUP BY h.id, h.title, h.description, h.frequency, h.start_date, h.end_date, h.created_at, h.user_id
//                        """, "ID", "Title", "Description", "Frequency", "Target Days", "Start Date", "End Date", "Created At", "User ID") +
//
//                // Habit Logs
//                buildTable("Habit Logs", "SELECT id, date, completed, habit_id FROM habit_logs",
//                        "ID", "Date", "Completed", "Habit ID") +
//
//                // Tags
//                buildTable("Tags", "SELECT id, name FROM habit_tags", "ID", "Name") +
//
//                // Habit-Tag Mapping
//                buildTable("Habit-Tag Mapping", "SELECT habit_id, tag_id FROM habit_tag_mapping",
//                        "Habit ID", "Tag ID") +
//                "</body></html>";
//        return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
//    }
//
//    /**
//     * Utility to build an HTML table for a given native SQL result.
//     */
//    private String buildTable(String title, String query, String... headers) {
//        StringBuilder table = new StringBuilder("<h2>").append(title).append("</h2>");
//        table.append("<table border='1'><tr>");
//        for (String header : headers) table.append("<th>").append(htmlEscape(header)).append("</th>");
//        table.append("</tr>");
//
//        List<Object[]> rows = entityManager.createNativeQuery(query).getResultList();
//        for (Object[] row : rows) {
//            table.append("<tr>");
//            for (Object col : row) {
//                table.append("<td>").append(htmlEscape(String.valueOf(col))).append("</td>");
//            }
//            table.append("</tr>");
//        }
//
//        table.append("</table><br>");
//        return table.toString();
//    }
//
//    /**
//     * Escapes HTML special characters.
//     */
//    private String htmlEscape(String input) {
//        return HtmlUtils.htmlEscape(input == null ? "" : input);
//    }
//}
