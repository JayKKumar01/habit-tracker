package com.jay.habit_tracker.dto;

import com.jay.habit_tracker.enums.Frequency;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitRequest {
    private String title;
    private String description;
    private Frequency frequency;
    private Set<DayOfWeek> targetDays;
    private LocalDate startDate;
}
