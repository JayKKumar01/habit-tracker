package com.jay.habit_tracker.mapper;

import com.jay.habit_tracker.dto.HabitRequestDTO;
import com.jay.habit_tracker.dto.HabitResponseDTO;
import com.jay.habit_tracker.entity.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // set manually in service
    Habit toEntity(HabitRequestDTO dto);

    @Mapping(source = "targetDays", target = "targetDays")
    HabitResponseDTO toDto(Habit habit);
}
