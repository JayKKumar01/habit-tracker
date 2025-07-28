package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.habit_log.HabitLogRequest;
import com.jay.habit_tracker.dto.habit_log.HabitLogResponse;

public interface HabitLogService {
    HabitLogResponse updateHabitLog(HabitLogRequest request);
}
