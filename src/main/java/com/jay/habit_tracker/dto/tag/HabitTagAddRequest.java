package com.jay.habit_tracker.dto.tag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitTagAddRequest {
    private Long habitId;
    private String name;
}
