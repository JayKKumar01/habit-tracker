package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.HabitLogRequest;
import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.entity.Habit;
import com.jay.habit_tracker.entity.HabitLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitLogMapper {
    HabitLogResponse toDto(HabitLogRequest request);
    @Mapping(source = "habit.id", target = "habitId") // ✅ Custom mapping
    HabitLogResponse toDto(HabitLog habitLog);
}
