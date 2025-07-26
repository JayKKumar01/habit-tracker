package com.jay.habit_tracker.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitLogResponse {
    private LocalDate date;
    private boolean completed;
}
