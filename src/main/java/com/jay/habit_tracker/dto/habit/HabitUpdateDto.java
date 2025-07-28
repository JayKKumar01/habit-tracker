package com.jay.habit_tracker.dto.habit;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HabitUpdateDto {
    private Long habitId;
    private String title;
    private String description;
    private LocalDate endDate;
}
