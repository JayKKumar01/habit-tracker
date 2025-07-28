package com.jay.habit_tracker.service;

import com.jay.habit_tracker.dto.HabitEditRequest;
import com.jay.habit_tracker.dto.HabitRequest;
import com.jay.habit_tracker.dto.HabitResponse;
import com.jay.habit_tracker.dto.HabitWithLogsResponse;

import java.util.List;

public interface HabitService {
    HabitResponse createHabit(String userEmail, HabitRequest habitRequest);
    boolean editHabit(HabitEditRequest editRequest);
    List<HabitResponse> getHabitsByUserId(Long userId);
    List<HabitWithLogsResponse> getHabitWithLogsByUserId(Long userId);
    boolean deleteHabit(Long habitId);

}
