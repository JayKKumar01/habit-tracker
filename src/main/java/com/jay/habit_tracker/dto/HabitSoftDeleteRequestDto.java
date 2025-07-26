package com.jay.habit_tracker.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class HabitSoftDeleteRequestDto {
    private Long id;
    private LocalDate endDate;
}
