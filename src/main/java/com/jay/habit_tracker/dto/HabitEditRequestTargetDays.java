package com.jay.habit_tracker.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HabitEditRequestTargetDays {
    private Long habitId;
    private Set<String> targetDays;
}
