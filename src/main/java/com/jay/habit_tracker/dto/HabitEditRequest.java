package com.jay.habit_tracker.dto;

import com.jay.habit_tracker.enums.Frequency;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HabitEditRequest {
    private Long habitId;
    private String title;
    private String description;
    private LocalDate endDate;
}
