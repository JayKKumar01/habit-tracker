package com.jay.habit_tracker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HabitSoftDeleteRequest {
    private Long id;
    private LocalDate endDate;
}
