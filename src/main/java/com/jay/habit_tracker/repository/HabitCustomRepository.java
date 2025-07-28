package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.dto.HabitResponse;

import java.util.List;

public interface HabitCustomRepository {
    List<HabitResponse> findHabitResponsesByUserId(Long userId);

    List<HabitLogResponse> findHabitLogResponsesByUserId(Long userId);
}
