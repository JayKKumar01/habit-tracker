package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit_log.HabitLogUpdateDto;

public interface HabitLogService {
    HabitLogUpdateDto updateHabitLog(HabitLogUpdateDto request);
}
