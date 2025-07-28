package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitLogRequest;
import com.jay.habit_tracker.dto.HabitLogResponse;

import java.util.List;

public interface HabitLogService {
    HabitLogResponse updateHabitLog(HabitLogRequest request);

    List<HabitLogResponse> getAllLogsForHabit(Long habitId);

    List<HabitLogResponse> getAllLogsForUser(String email);

    List<HabitLogResponse> getAllLogsForUserId(Long userId);
}
