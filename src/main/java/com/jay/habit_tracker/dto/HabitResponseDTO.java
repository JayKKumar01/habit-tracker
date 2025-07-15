package com.jay.habit_tracker.dto;

import com.jay.habit_tracker.enums.Frequency;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Frequency frequency;
    private Set<String> targetDays; // e.g., ["MONDAY", "WEDNESDAY"]
    private LocalDate startDate;
    private int currentStreak;
    private double completionRate;
}
