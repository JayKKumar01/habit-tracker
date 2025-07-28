package com.jay.habit_tracker.dto.habit_log;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitLogUpdateDto {
    private Long habitId;
    private LocalDate date;
    private boolean completed;
}
