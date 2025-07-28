package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.HabitEditRequest;
import com.jay.habit_tracker.dto.HabitEditResponse;
import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.entity.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // set manually in service
    Habit toEntity(HabitRequest dto);

    @Mapping(source = "targetDays", target = "targetDays")
    HabitResponse toDto(Habit habit);

    HabitEditResponse toEditResponse(HabitEditRequest editRequest);
}
