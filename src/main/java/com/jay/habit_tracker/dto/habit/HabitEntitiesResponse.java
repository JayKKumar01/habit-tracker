package com.jay.habit_tracker.dto.habit;

import com.jay.habit_tracker.dto.habit_log.HabitLogDto;
import com.jay.habit_tracker.dto.tag.HabitTagDto;
import com.jay.habit_tracker.enums.Frequency;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitEntitiesResponse {
    private Long id;
    private String title;
    private String description;
    private Frequency frequency;
    private Set<DayOfWeek> targetDays;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // ✅ List of associated log entries
    private List<HabitLogDto> logs;
    private List<HabitTagDto> tags;
}
