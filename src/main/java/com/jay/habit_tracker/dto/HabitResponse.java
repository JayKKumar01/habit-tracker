package com.jay.habit_tracker.dto;

import com.jay.habit_tracker.enums.Frequency;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitResponse {
    private Long id;
    private String title;
    private String description;
    private Frequency frequency;
    private Set<String> targetDays; // e.g., ["MONDAY", "WEDNESDAY"]
    private LocalDate startDate;
    private LocalDate endDate;
    private int currentStreak;
    private double completionRate;
}
