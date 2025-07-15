package com.jay.habit_tracker.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitLogResponseDTO {
    private LocalDate date;
    private boolean completed;
}
