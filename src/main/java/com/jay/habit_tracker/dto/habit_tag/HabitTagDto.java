package com.jay.habit_tracker.dto.habit_tag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitTagDto {
    private Long id;
    private String name;
}
