package com.jay.habit_tracker.repository;

import com.jay.habit_tracker.dto.HabitLogResponse;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.dto.HabitWithLogsResponse;

import java.time.LocalDate;
import java.util.List;

public interface HabitCustomRepository {
    List<HabitResponse> findHabitResponsesByUserId(Long userId);

    List<HabitLogResponse> findHabitLogResponsesByUserId(Long userId);

    HabitLogResponse upsertHabitLog(Long habitId, LocalDate date, boolean completed);

    // ✅ New method for combined response
    List<HabitWithLogsResponse> findHabitWithLogsByUserId(Long userId);
}
