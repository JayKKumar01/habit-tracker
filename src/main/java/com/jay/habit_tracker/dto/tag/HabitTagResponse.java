package com.jay.habit_tracker.dto.tag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitTagResponse {
    private Long id;
    private String name;
}
