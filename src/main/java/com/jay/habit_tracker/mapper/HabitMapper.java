package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.entity.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // set manually in service
    Habit toEntity(HabitRequest dto);

    @Mapping(source = "targetDays", target = "targetDays")
    @Mapping(source = "endDate", target = "endDate")
    HabitResponse toDto(Habit habit);
}
