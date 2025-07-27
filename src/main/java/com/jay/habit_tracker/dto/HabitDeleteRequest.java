package com.jay.habit_tracker.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HabitDeleteRequest {
    private Long habitId;
}
