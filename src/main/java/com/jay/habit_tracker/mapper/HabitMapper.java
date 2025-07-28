package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.habit.HabitRequest;
import com.jay.habit_tracker.dto.habit.HabitResponse;
import com.jay.habit_tracker.entity.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(source = "targetDays", target = "targetDays")
    HabitResponse toDto(Habit habit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // set manually in service
    Habit toEntity(HabitRequest dto);
}
