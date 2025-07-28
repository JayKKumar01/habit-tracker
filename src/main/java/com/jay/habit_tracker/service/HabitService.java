package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.*;

import java.util.List;

public interface HabitService {
    HabitResponse createHabit(Long userId, HabitRequest habitRequest);
    HabitEditResponse editHabit(HabitEditRequest editRequest);
    List<HabitWithLogsResponse> getHabitWithLogsByUserId(Long userId);
    boolean deleteHabit(Long habitId);

}
